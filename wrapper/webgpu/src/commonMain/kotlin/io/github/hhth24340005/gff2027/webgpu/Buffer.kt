package io.github.hhth24340005.gff2027.webgpu

/** Memory on the GPU that shaders read. Obtained from [Device.createBuffer]. */
public expect class Buffer : BindingResource

/** A way a [Buffer] may be used. */
public enum class BufferUsage {
  /** [Queue.writeBuffer] can fill it. */
  CopyDst,

  /** Shaders read it as uniforms, as `var<uniform>`. */
  Uniform,
}
