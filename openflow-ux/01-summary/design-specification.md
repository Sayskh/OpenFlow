# Spesifikasi Desain Layout OpenFlow

Desain antarmuka modular mobile video editor berbasis Jetpack Compose M3.
Resolusi acuan: 393 x 852 dp (Densitas standar Android 1080x2340 @ 440dpi).

---

## 1. Main Editor Shell

### Pembagian Ruang Vertikal (Portrait)
- Top Bar: 48 dp (Project title, undo/redo, export action).
- Preview Viewport: 270 dp (Letterbox 16:9 / 9:16 / 1:1, safe margin overlay, transform handles).
- Transport Toolbar: 48 dp (Frame step, play/pause, timecode display `HH:MM:SS:FF`).
- Timeline Section: Sisa tinggi layar (sekitar 430 dp).
  - Ruler bar: 28 dp.
  - Track header control bar: 36 dp.
  - Multi-track scrollable canvas: sisa tinggi.

### Wireframe Shell Editor
```
+--------------------------------------------------+ [393 dp]
| [<-] Project_01            [Undo] [Redo] [Export] | 48 dp TopAppBar
+--------------------------------------------------+
|                                                  |
|                  VIEWPORT                        |
|             (Preview Letterbox)                  | 270 dp
|      [ Safe Area Guides: 90% Action / 80% Title ]|
|            [ Transform Bounding Box ]            |
|                                                  |
+--------------------------------------------------+
| [-1f]  [ |< ]     [ > Play ]     [ >| ]  [+1f]   | 48 dp Transport
| Timecode: 00:01:24:12 / 00:03:00:00   [Split]    |
+--------------------------------------------------+
| RULER: |---|---|---|---|---|---|---|---|---|---| | 28 dp Ruler
+--------------------------------------------------+
| [Layer 3: Text]     |===== Title Text =====|     | 48 dp Track 3
| [Layer 2: FX GLSL]  |   [Curve: FadeIn]    |     | 40 dp Track 2
| [Layer 1: Video]    |[Thumb][Thumb][Thumb] |     | 64 dp Track 1
| [Audio: Waveform]   |/\/\/\/\/\/\/\/\/\/\/\|     | 48 dp Audio
|                     ^ PLAYHEAD (Center/Scrub)     |
+--------------------------------------------------+
| [Tracks] [Effects] [Curves] [Inspector] [Audio]  | 56 dp BottomNav
+--------------------------------------------------+
```

### Detail Komponen Shell:
1. Preview Viewport:
   - Surface: `#0D0D0D` (Pure dark letterbox).
   - Safe Action Area: Garis putus-putus `#737373` tebal 1 dp pada batas 90%.
   - Safe Title Area: Garis putus-putus `#525252` tebal 1 dp pada batas 80%.
   - Transform Handles: 4 sudut kotak (size 12 dp solid rect) + 1 rotasi anchor handle di atas (lingkaran 16 dp garis konektor 24 dp).
2. Transport Bar:
   - Tombol Step Back / Step Forward: 48x48 dp touch target, ikon step frame (1/30s atau 1/60s).
   - Timecode Display: Font monospaced `JetBrains Mono` 13 sp, bobot medium. Menampilkan jam, menit, detik, frame.
   - Play/Pause Button: Floating style tonal icon button, diameter 44 dp (touch target 48 dp).
3. Multi-Track Timeline:
   - Ruler: Tick mark setiap 5 frame (kecil 4 dp) dan 1 detik (besar 8 dp dengan label detik).
   - Playhead: Jarum merah netral `#FF6B6B`, garis tebal 1.5 dp, kepala segitiga terbalik 12x10 dp di atas ruler.
   - Track Video: Strip klip dengan tinggi 64 dp, menampilkan strip thumbnail frame setiap interval zoom.
   - Track Audio: Tinggi 48 dp, latar `#1C1C1C`, waveform dirender via path Canvas `#8AB4F8`.
   - Track Header: Indikator ikon lock, hide/mute, expand/collapse track.

---

## 2. Keyframe & Bezier Curve Editor

Overlay kurva muncul saat parameter properti memiliki keyframe aktif dan pengguna menekan ikon kurva.

### Wireframe Curve Editor (Modal Bottom Sheet / Full Overlay)
```
+--------------------------------------------------+
| [X] Curve Editor: Transform.Position.X     [Done]| 48 dp Header
+--------------------------------------------------+
| 1.0 +------------------------------------------+ |
|     |                   O (Handle B: [0.2, 1]) | |
|     |                  /                       | | 240 dp
|     |        Curve -> /                        | | Bezier Canvas
|     |                /                         | | (Grid 0.0 - 1.0)
|     |  (Handle A)   /                          | |
| 0.0 +------O-------+---------------------------+ |
|     0.0                                    1.0   |
+--------------------------------------------------+
| Values: In [0.42, 0.00] | Out [0.58, 1.00]       | 32 dp Info Bar
+--------------------------------------------------+
| Presets: [Linear] [Ease In] [Ease Out] [Bezier]  | 48 dp Preset Bar
| Actions: [Split Tangents] [Reset] [Copy Curve]   | 48 dp Action Bar
+--------------------------------------------------+
```

### Spesifikasi Interaksi Kurva:
- Handle A (In) dan Handle B (Out): Lingkaran berdiameter 16 dp, fill `#E5E2E1`, stroke `#1D1B1A` 2 dp.
- Hitbox sentuh handle: Minimum 48x48 dp berpusat pada koordinat handle.
- Tangent Lines: Garis tebal 1.5 dp `#A8A29E` menghubungkan node keyframe ke handle kontrol.
- Preset Cepat: Chip pilihan kurva standar CSS `cubic-bezier` (Linear, Ease-In, Ease-Out, Ease-InOut).

---

## 3. Inspector / Property Sheet

Bottom sheet persisten yang dapat digeser naik (peek: 220 dp, half: 400 dp, full: 680 dp).

### Wireframe Inspector
```
+--------------------------------------------------+
| [= Handle =]  Clip: Video_01.mp4          [Lock] | 40 dp Drag handle
+--------------------------------------------------+
| [Transform]  [Effects Rack]  [Masking]  [Blending]| 44 dp TabRow
+--------------------------------------------------+
| Position X: [-120.0 dp]  [<>] (Keyframe active)  | 48 dp Row
| Slider: [----|-----------------]  Reset: [0]     |
| Position Y: [ +45.0 dp]  [<>]                    | 48 dp Row
| Scale:      [ 100 %   ]  [Lock Aspect] [<>]      | 48 dp Row
| Rotation:   [ 12.5 deg]  [<>]                    | 48 dp Row
| Opacity:    [  85 %   ]  [<>]                    | 48 dp Row
| Anchor:     Center (0.5, 0.5)                    | 40 dp Row
+--------------------------------------------------+
| GLSL EFFECTS RACK                                |
| [::] Gaussian Blur       [Eye] [Bypass] [Trash]  | 52 dp Effect item
|      Radius: [----|------] 12.0 px               |
| [::] Chromatic Aberr.    [Eye] [Bypass] [Trash]  | 52 dp Effect item
| [+ Add Shader / Effect]                          | 48 dp Add button
+--------------------------------------------------+
```

### Kontrol Masukan Properti:
- Input Numerik: Tap membuka input keyboard numerik presisi; drag horizontal pada kotak teks bertindak sebagai virtual scrubber (kepekaan: 1 dp drag = 0.5 unit nilai).
- Keyframe Toggle `<>`: Tombol belah ketupat (diamond icon). State aktif warna `#FFD066`, state non-aktif outline `#737373`.
- Effect Item: Reorder handle `[::]` pada sisi kiri untuk drag-and-drop urutan shader pass.

---

## 4. Expression Editor (QuickJS Integration)

Editor kode monospaced untuk mengontrol animasi prosedural menggunakan sintaks JavaScript ES2020.

### Wireframe Expression Editor
```
+--------------------------------------------------+
| [Cancel] Expression: Transform.Rotation   [Apply]| 48 dp Header
+--------------------------------------------------+
| 1 | // Wobble rotation using time and frequency  |
| 2 | const freq = 2.5;                            | Code canvas
| 3 | const amp = 15.0;                            | (Monospace,
| 4 | Math.sin(time * freq * Math.PI * 2) * amp;   |  Line numbers)
| 5 |                                              |
+--------------------------------------------------+
| [time] [value] [index] [fps] [sin()] [clamp()]   | 44 dp Chip Bar
+--------------------------------------------------+
| [Console: OK] [Eval time: 0.12ms]                | 28 dp Status Bar
+--------------------------------------------------+
```

### Fitur Spesifik:
- Monospace Typography: `JetBrains Mono` atau Roboto Mono 12 sp.
- Chip Bar: Injeksi cepat parameter bawaan engine (`time`, `value`, `width`, `height`, `index`, `fps`) tanpa perlu mengetik simbol khusus di keyboard mobile.
- Status Bar: Umpan balik real-time dari engine QuickJS. Jika sintaks keliru, baris berubah menjadi latar `#4E1B1B` dengan pesan error baris bersangkutan.

---

## 5. Export Sheet

Bottom modal sheet untuk konfigurasi render pipeline dan penulisan media container.

### Wireframe Export Sheet
```
+--------------------------------------------------+
| [X] Export Configuration                 [Start] | 56 dp TitleBar
+--------------------------------------------------+
| PRESET: [Custom v]                               |
| Resolution:                                      |
| ( ) 720p HD      (*) 1080p FHD      ( ) 4K UHD   | 48 dp Segmented
|                                                  |
| Frame Rate:                                      |
| ( ) 24 fps       (*) 30 fps         ( ) 60 fps   | 48 dp Segmented
|                                                  |
| Codec:                                           |
| (*) H.264 (AVC)           ( ) H.265 (HEVC)       | 48 dp RadioRow
|                                                  |
| Target Bitrate:                                  |
| [====================|-------]  18.5 Mbps        | 48 dp Slider
|                                                  |
| Estimasi Ukuran Berkas: ~ 124.8 MB               | 36 dp Summary
| Durasi Total: 00:00:54:00                        |
+--------------------------------------------------+
| [        Render & Export Video        ]          | 52 dp Primary Btn
+--------------------------------------------------+
```

---

## Touch Target & Ergonomi Thumb Zone
- Zona Primer (0 hingga 340 dp dari dasar layar): Transport controls, timeline scrub, inspector sliders, curve handles.
- Zona Sekunder (340 hingga 600 dp dari dasar layar): Viewport, timeline track visual, ruler.
- Zona Atas (600 dp ke atas): Action global jarang ditekan (Undo, Redo, Project Settings, Export Menu).
- Seluruh elemen yang dapat diklik memiliki minimum bounding box 48 x 48 dp terlepas dari ukuran visual ikon.
