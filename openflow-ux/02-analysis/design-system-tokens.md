# Design System Tokens: OpenFlow (Material 3 Dark Mode)

Sistem token warna netral mutlak D65 untuk akurasi grading video tanpa kontaminasi saturasi warna antarmuka.

---

## 1. Material 3 Color Tokens (Neutral D65 Dark Palette)

Prinsip: Latar belakang dan kontainer menggunakan skala abu-abu murni (sRGB Chromaticity x=0.3127, y=0.3290) untuk mencegah ilusi optik bias temperatur warna pada video preview.

### Surface & Background Tokens
| Token M3 | Hex Code | Luminance | Penggunaan |
|---|---|---|---|
| `colorSurface` | `#121212` | 7% | Latar belakang dasar editor shell |
| `colorSurfaceDim` | `#0E0E0E` | 5% | Latar viewport preview letterbox |
| `colorSurfaceBright` | `#262626` | 15% | Kontainer fokus atau card aktif |
| `colorSurfaceContainerLowest` | `#0A0A0A` | 4% | Area track timeline terdalam |
| `colorSurfaceContainerLow` | `#171717` | 9% | Latar track strip video/audio |
| `colorSurfaceContainer` | `#1E1E1E` | 12% | Bottom sheet inspector, toolbar dasar |
| `colorSurfaceContainerHigh` | `#282828` | 16% | Dialog modal, popup context menu |
| `colorSurfaceContainerHighest` | `#333333` | 20% | Input fields, active chips background |

### Content & Stroke Tokens
| Token M3 | Hex Code | Kontras thd Surface | Penggunaan |
|---|---|---|---|
| `colorOnSurface` | `#EEEEEE` | 13.5:1 (AAA) | Teks judul utama, timecode aktif |
| `colorOnSurfaceVariant` | `#A3A3A3` | 7.2:1 (AAA) | Label sekunder, ikon non-aktif, ruler ticks |
| `colorOutline` | `#525252` | 3.5:1 | Batas border track, divider toolbar |
| `colorOutlineVariant` | `#333333` | 2.1:1 | Grid garis curve editor, safe margin 80% |

### Semantic & Accent Tokens (Restricted Saturation)
| Token M3 | Hex Code | Penggunaan |
|---|---|---|
| `colorPrimary` | `#93C5FD` | Playhead indicator, selected clip border, active tab |
| `colorOnPrimary` | `#082F49` | Teks di atas warna primary |
| `colorPrimaryContainer` | `#1E3A8A` | Latar clip terpilih pada timeline |
| `colorOnPrimaryContainer` | `#DBEAFE` | Teks judul klip terpilih |
| `colorSecondary` | `#A3E635` | Indikator audio waveform, level meter aman (-6dB) |
| `colorTertiary` | `#FBBF24` | Indikator keyframe diamond aktif, kurva bezier |
| `colorError` | `#F87171` | Warning audio clipping, parser error QuickJS |
| `colorOnError` | `#450A0A` | Teks peringatan di atas error |

---

## 2. Typography Tokens

Spesifikasi tipografi menggunakan font sistem Android `Roboto` untuk UI dan `JetBrains Mono` untuk timecode serta kode ekspresi.

| Role | Font Family | Size (sp) | Weight | Line Height (sp) | Tracking |
|---|---|---|---|---|---|
| `displaySmall` | Roboto | 24 | Regular | 32 | 0 |
| `headlineSmall` | Roboto | 18 | Medium | 24 | 0 |
| `titleMedium` | Roboto | 14 | SemiBold | 20 | +0.1 |
| `bodyLarge` | Roboto | 14 | Regular | 20 | +0.25 |
| `bodySmall` | Roboto | 12 | Regular | 16 | +0.4 |
| `labelLarge` | Roboto | 12 | Medium | 16 | +0.1 |
| `labelMedium` | Roboto | 11 | Medium | 16 | +0.5 |
| `codeTimecode` | JetBrains Mono | 13 | SemiBold | 16 | 0 |
| `codeExpression` | JetBrains Mono | 12 | Regular | 18 | -0.2 |

---

## 3. Spacing & Touch Dimensions

Skala grid kelipatan 4 dp dan 8 dp:

| Token Spacing | Dimensi | Penggunaan |
|---|---|---|
| `spacingNone` | 0 dp | Flush borders |
| `spacingXxs` | 2 dp | Playhead line width, border stroke thickness |
| `spacingXs` | 4 dp | Padding antar chip ekspresi, margin thumbnail strip |
| `spacingSm` | 8 dp | Padding internal tombol, margin antar track |
| `spacingMd` | 16 dp | Padding sisi layar, margin container inspector |
| `spacingLg` | 24 dp | Pemisah antar section dalam export sheet |
| `spacingXl` | 32 dp | Spacer navigasi utama |
| `touchTargetMin` | 48 dp | Area sentuh minimum untuk seluruh interaksi jari |

---

## 4. Corner Radius Tokens (Material 3 Shapes)

| Token Shape | Nilai Radius | Penggunaan |
|---|---|---|
| `shapeNone` | 0 dp | Viewport preview, timeline canvas, playhead needle |
| `shapeXs` | 4 dp | Handle clip trim, track selection indicator |
| `shapeSm` | 8 dp | Video thumbnail card, input numeric box |
| `shapeMd` | 12 dp | Action buttons, chip selectors |
| `shapeLg` | 16 dp | Bottom sheet top corners (Inspector, Export) |
| `shapeFull` | 999 dp | Play/pause floating pill, keyframe curve handle |

---

## 5. Elevation & Layer Z-Index Tokens

| Layer | Z-Index / Tonal Elevation | Komponen |
|---|---|---|
| `zViewport` | 0 | Hardware SurfaceView video playback |
| `zTimelineTracks` | 1 | Clip bars, audio waveforms |
| `zTimelinePlayhead` | 10 | Playhead needle and scrubbing guide |
| `zOverlayControls` | 20 | Viewport transform handles, safe area overlays |
| `zToolbars` | 30 | Transport controls, top app bar |
| `zBottomSheet` | 40 | Inspector property sheet, expression editor |
| `zModalDialog` | 50 | Export progress, critical warnings |
