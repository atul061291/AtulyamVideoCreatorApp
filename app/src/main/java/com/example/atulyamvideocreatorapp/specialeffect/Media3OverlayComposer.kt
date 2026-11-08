package com.example.atulyamvideocreatorapp.specialeffect

import android.content.Context
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

@UnstableApi
class Media3OverlayComposer(
    private val animation: CustomAnimation
) : GlEffect {
    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram {
        return CustomShaderImpl(useHdr, animation)
    }
}

@UnstableApi
private class CustomShaderImpl(
    useHdr: Boolean,
    private val animation: CustomAnimation
) : BaseGlShaderProgram(useHdr, /* texturePoolSize= */ 1) {

    private var glProgram: GlProgram? = null

    private val VERTEX_SHADER = """
        attribute vec4 aPosition;
        attribute vec4 aTexPosition;
        varying vec2 vTexSamplingCoords;
        void main() {
            gl_Position = aPosition;
            vTexSamplingCoords = aTexPosition.xy;
        }
    """.trimIndent()

    // ENHANCED GLSL SHADER: Uses explicit high-contrast glowing filters for overlays
    private val FRAGMENT_SHADER = """
        precision mediump float;
        varying vec2 vTexSamplingCoords;
        uniform sampler2D uTexSampler;
        uniform float uTime;
        uniform int uEffectType;

        float hash(vec2 p) {
            return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
        }

        float noise(vec2 p) {
            vec2 i = floor(p);
            vec2 f = fract(p);
            vec2 u = f*f*(3.0-2.0*f);
            return mix(mix(hash(i + vec2(0.0,0.0)), hash(i + vec2(1.0,0.0)), u.x),
                       mix(hash(i + vec2(0.0,1.0)), hash(i + vec2(1.0,1.0)), u.x), u.y);
        }

        void main() {
            vec4 baseColor = texture2D(uTexSampler, vTexSamplingCoords);
            
            if (uEffectType == 0) { // --- TWINKLE PARTICLES ---
                // Add a constant moving speed drift vector to float particles all around
                vec2 drift = vec2(uTime * 0.08, uTime * 0.05); 
                vec2 uv = (vTexSamplingCoords * 28.0) + drift; // Density grid adjustment
                vec2 id = floor(uv);
                float n = hash(id);
                
                vec3 particleColor = vec3(0.0);
                if (n > 0.85) { // Increases overall count footprint density
                    // Rhythmic blinking calculation sequence loop
                    float blink = sin(uTime * 4.5 + n * 6.28) * 0.5 + 0.5;
                    // Sharp focal dot mask generation
                    float starDot = smoothstep(0.18, 0.0, length(fract(uv) - 0.5));
                    
                    // Force a vibrant white glow factor layer
                    particleColor = vec3(1.0, 1.0, 1.0) * starDot * blink * 1.5;
                }
                
                // Additive overlay blend injection logic: forces stars directly onto the visual pixels
                gl_FragColor = vec4(baseColor.rgb + particleColor, baseColor.a);
                
            } else if (uEffectType == 1) { // --- FUME SMOKE ---
                vec2 uv = vTexSamplingCoords;
                float smokeLayer = noise(uv * 2.5 + vec2(uTime * 0.1, uTime * -0.15));
                smokeLayer += noise(uv * 5.0 + vec2(uTime * -0.05, uTime * 0.08)) * 0.5;
                
                vec3 smokeColor = vec3(0.88, 0.88, 0.88); 
                float intensity = clamp(smokeLayer * 0.25, 0.0, 1.0); 
                
                gl_FragColor = vec4(mix(baseColor.rgb, smokeColor, intensity), baseColor.a);
            } else {
                gl_FragColor = baseColor;
            }
        }
    """.trimIndent()

    init {
        try {
            glProgram = GlProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        } catch (e: Exception) {
            throw VideoFrameProcessingException(e)
        }
    }

    override fun configure(inputWidth: Int, inputHeight: Int): androidx.media3.common.util.Size {
        return androidx.media3.common.util.Size(inputWidth, inputHeight)
    }

    override fun drawFrame(inputTextureId: Int, presentationTimeUs: Long) {
        val program = glProgram ?: return
        program.use()

        // Continuous timestamp calculation metric mapping
        val runningTimeSec = presentationTimeUs / 1_000_000f
        program.setFloatUniform("uTime", runningTimeSec)

        val typeId = when (animation) {
            is TwinkleParticleTransformation -> 0
            is FumeSmokeTransformation -> 1
            else -> -1
        }
        program.setIntUniform("uEffectType", typeId)

        // Bind texture configuration
        program.setSamplerTexIdUniform("uTexSampler", inputTextureId, /* texUnitIndex= */ 0)

        // Map layout boundaries
        program.setBufferAttribute(
            "aPosition",
            androidx.media3.common.util.GlUtil.getNormalizedCoordinateBounds(),
            androidx.media3.common.util.GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE
        )
        program.setBufferAttribute(
            "aTexPosition",
            androidx.media3.common.util.GlUtil.getTextureCoordinateBounds(),
            androidx.media3.common.util.GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE
        )

        // FIX: Force turn on the hardware blending system so overlays draw correctly on top of the images
        android.opengl.GLES20.glEnable(android.opengl.GLES20.GL_BLEND)
        android.opengl.GLES20.glBlendFunc(
            android.opengl.GLES20.GL_SRC_ALPHA,
            android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA
        )

        program.bindAttributesAndUniforms()
        android.opengl.GLES20.glDrawArrays(android.opengl.GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // Turn off blending state safely to avoid affecting later tracking operations
        android.opengl.GLES20.glDisable(android.opengl.GLES20.GL_BLEND)
    }

    override fun release() {
        super.release()
        try {
            glProgram?.delete()
        } catch (e: Exception) {
            // Suppress errors during thread execution cleanup
        }
    }
}
