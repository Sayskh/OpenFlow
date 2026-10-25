# Component Specifications: OpenFlow

Spesifikasi status interaktif, properti visual, dan parameter gestur untuk komponen kunci OpenFlow.

---

## 1. Timeline Clip Strip & Playhead

### Status Interaksi Clip Strip
- Idle: Latar `#171717`, border stroke 1 dp `#333333`, thumbnail ter-render dengan opasitas 100%.
- Pressed: Elevasi tonal naik ke `#262626`, feedback haptic ringan `HapticFeedbackType.TextHandleMove`.
- Selected: Border stroke 2 dp `#93C5FD` (Primary), handle trim kiri dan kanan muncul dengan ketebalan 12 dp berwarna `#93C5FD`.
- Dragging / Trimming: Handle trim membesar menjadi 16 dp lebar sentuh, tooltip floating menampilkan delta frame `[-14 frames (-00:00:00:14)]`.

### Status Interaksi Playhead
- Idle: Jarum vertikal 1.5 dp `#FF6B6B`, segitiga penunjuk atas `#FF6B6B`.
- Scrubbing: Garis vertikal bertambah tebal menjadi 2.5 dp, guideline garis putus-putus transparan menyala menembus seluruh layer track. Haptic tick setiap snapping mengenai ujung klip atau marker.

---

## 2. Dual-Handle Cubic Bezier Canvas

Komponen editor grafik kurva kecepatan dan nilai.

### Elemen Visual:
- Grid Kanvas: Garis horizontal dan vertikal setiap 0.25 unit dengan garis `#282828`. Garis nilai 0.0 dan 1.0 ditebalkan `#525252`.
- Node Keyframe A (Start `[0, 0]`) dan B (End `[1, 1]`): Lingkaran solid 10 dp `#EEEEEE`.
- Handle Tangent In & Out: Lingkaran 16 dp `#FBBF24` (Tertiary) dengan garis konektor tangent 1.5 dp `#FBBF24`.
- Bounding Box Sentuh: 48x48 dp berpusat pada masing-masing handle.

### Status Handle:
- Idle: Fill `#FBBF24` solid, stroke 0 dp.
- Active Drag: Fill `#F59E0B`, stroke 3 dp `#FFFFFF`, pembesaran visual handle menjadi diameter 20 dp.
- Snapped: Handle mengunci otomatis ke sumbu horizontal/vertikal (0.0, 0.5, 1.0) jika berada dalam toleransi radius 8 dp.

---

## 3. Inspector Property Row & Virtual Slider

### Status Row:
- Idle: Kontainer `#1E1E1E`, label properti `#A3A3A3`, nilai numerik dalam kotak `#282828`.
- Slider Scrubbing (Virtual Drag): Pengguna menyapu jari secara horizontal pada kotak teks. Nilai bertambah/berkurang proporsional terhadap kecepatan drag. Latar kotak berubah menjadi `#333333` dengan aksen border bawah `#93C5FD`.
- Direct Text Edit: Tap sekali pada angka membuka software keyboard numerik, seluruh angka terpilih otomatis (select all).

### Keyframe Diamond Button:
- Inactive (No Keyframe at current frame): Ikon outline belah ketupat `#737373`.
- Active (Keyframe exists at playhead): Ikon solid belah ketupat `#FBBF24`.
- Interpolated (Between two keyframes): Ikon outline belah ketupat dengan titik kecil di tengah `#FBBF24`.

---

## 4. Expression Editor & Parameter Chips

### Code Editor Canvas:
- Background: `#0E0E0E` (SurfaceDim).
- Text: Monospaced `JetBrains Mono` 12 sp, regular, warna sintaks:
  - Keyword (`const`, `let`, `function`, `return`): `#93C5FD`
  - Built-in parameter (`time`, `value`, `fps`): `#FBBF24`
  - Number literal (`10.5`, `2.0`): `#A3E635`
  - Operator (`+`, `-`, `*`, `/`): `#EEEEEE`
  - Comment (`// ...`): `#666666`
- Error State: Baris yang mengalami parse error QuickJS mendapatkan latar `#3B1212` dan penanda garis bergelombang merah `#F87171`.

### Parameter Chips:
- Tonal Chip M3: Tinggi 32 dp, radius 8 dp, latar `#282828`.
- Tap Action: Menginjeksi teks parameter secara instan ke posisi kursor editor aktif tanpa memicu reload keyboard.

---

## 5. Spesifikasi Gestur & Motion

### Timeline Zoom (2-Finger Pinch):
- Gestur: Dual-touch pinch in / pinch out pada area track timeline.
- Skala: 1 detik dapat diregangkan dari 20 dp (zoom out makro) hingga 480 dp (zoom in tingkat frame individual).
- Pivot: Titik tengah antara dua jari menjadi pusat pembesaran waktu.
- Motion Curve: `StandardEasing` (FastOutSlowIn), durasi 0 ms (interaksi real-time tanpa lag animasi).

### Magnet Snapping:
- Radius Snapping: 12 dp dari playhead ke tepi klip atau marker terdekat.
- Haptic: `HapticFeedbackType.SegmentTick` saat posisi klip tertarik ke magnet.
- Visual Cue: Garis snap vertikal tipis berwarna `#93C5FD` menyala sesaat.

### Long-Press Track Reorder:
- Ambang Waktu: 400 ms penahanan jari pada header track.
- Feedback: Elevasi track naik menjadi 8 dp, bayangan drop shadow aktif, klip terangkat sedikit (skala 102%), haptic `LongPress` bergetar.
- Reordering: Pengguna dapat menggeser posisi track ke atas atau ke bawah untuk mengubah layer stacking order rendering.
