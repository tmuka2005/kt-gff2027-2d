package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * A logical connection to an [Adapter] that creates GPU objects.
 * Obtained from [Adapter.requestDevice].
 */
public expect class Device {
  /**
   * Compiles [wgsl], a shader written in [WGSL](https://www.w3.org/TR/WGSL/).
   * The scope releases the module when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createShaderModule(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    wgsl: String,
    label: String? = null,
  ): ShaderModule
}
