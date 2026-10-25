package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Procedural property expression binding (After Effects-style).
 */
@Serializable
data class Expression(
    val parameterTarget: String,
    val expression: String,
    val isEnabled: Boolean = true
)
