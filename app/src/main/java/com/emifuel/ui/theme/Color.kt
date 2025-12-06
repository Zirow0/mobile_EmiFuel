package com.emifuel.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors
val Primary = Color(0xFF1976D2)         //0x1976D2
val PrimaryDark = Color(0xFF1565C0)     //0x1565C0
val PrimaryLight = Color(0xFFBBDEFB)    //0xBBDEFB

// Accent Colors
val Accent = Color(0xFFFFC107)          //0xFFC107
val AccentDark = Color(0xFFFFA000)      //0xFFA000

// Background & Surface
val Background = Color(0xFFFFFFFF)      //0xFFFFFF
val Surface = Color(0xFFFFFFFF)         //0xFFFFFF

// Text Colors
val TextPrimary = Color(0xFF212121)     //0x212121
val TextSecondary = Color(0xFF757575)   //0x757575
val TextHint = Color(0xFFBDBDBD)        //0xBDBDBD

// Status Colors
val Success = Color(0xFF4CAF50)         //0x4CAF50
val Warning = Color(0xFFFFC107)         //0xFFC107
val Error = Color(0xFFF44336)           //0xF44336

// Utility
val Divider = Color(0xFFE0E0E0)         //0xE0E0E0

// Light theme colors for Material 3
val md_theme_light_primary = Primary
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = PrimaryLight
val md_theme_light_onPrimaryContainer = PrimaryDark

val md_theme_light_secondary = Accent
val md_theme_light_onSecondary = Color(0xFF000000)
val md_theme_light_secondaryContainer = Color(0xFFFFECB3)
val md_theme_light_onSecondaryContainer = AccentDark

val md_theme_light_tertiary = Success
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC8E6C9)
val md_theme_light_onTertiaryContainer = Color(0xFF2E7D32)

val md_theme_light_background = Background
val md_theme_light_onBackground = TextPrimary
val md_theme_light_surface = Surface
val md_theme_light_onSurface = TextPrimary

val md_theme_light_error = Error
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFCDD2)
val md_theme_light_onErrorContainer = Color(0xFFC62828)

val md_theme_light_outline = Divider
val md_theme_light_outlineVariant = Color(0xFFF5F5F5)
val md_theme_light_surfaceVariant = Color(0xFFEAEAEA)       //0xCEAEAEA
val md_theme_light_onSurfaceVariant = TextSecondary
