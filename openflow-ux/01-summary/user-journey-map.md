# User Journey Map: OpenFlow

Pemetaan alur pengguna utama, transisi status, titik friksi, dan jalur pemulihan error.

---

## 1. Primary Video Editing Journey

```
[Import Media] 
     │
     ▼
[Timeline Stacking] ──► [Scrub to Target Frame] ──► [Split / Trim Clip]
                             │                            │
                             ▼                            ▼
                      [Transform / Pan]            [Ripple Adjust]
                             │
                             ▼
                    [Playback Preview]
```

### Tahapan:
1. Import Media:
   - Tindakan: Pengguna menekan tombol `+` pada timeline atau menggeser file dari storage picker.
   - Umpan Balik: Thumbnail strip langsung tergenerasi asynchronous via background thread coroutine, waveform audio di-cache.
2. Scrubbing & Trimming:
   - Tindakan: Menarik playhead atau drag edge handle pada klip video.
   - Umpan Balik: Haptic tick ringan saat frame berpindah, frame preview viewport ter-render real-time via hardware decoder SurfaceView.
3. Split Action:
   - Tindakan: Tekan tombol `Split` pada toolbar transport.
   - Umpan Balik: Klip terpotong pada posisi frame playhead tanpa perubahan posisi relatif waktu.

---

## 2. Keyframe & Bezier Curve Workflow

```
[Select Track/Clip]
     │
     ▼
[Open Inspector] ──► [Tap Keyframe Diamond on Property]
                             │
                             ▼
                     [Move Playhead + Change Value]
                             │
                             ▼
                     [Tap Curve Icon]
                             │
                             ▼
               [Manipulate Bezier Handles]
                             │
                             ▼
                 [Preview Animation Loop]
```

### Tahapan:
1. Inisiasi Keyframe:
   - Pengguna menavigasi ke properti `Transform.Position`.
   - Menekan ikon diamond `<>`. Warna ikon berubah menjadi `#FFD066` menandakan keyframe dibuat pada frame aktif.
2. Interpolasi Kurva:
   - Pengguna membuka Curve Canvas Overlay.
   - Menggeser Handle A atau Handle B untuk mengatur kurva akselerasi/deselerasi.
   - Canvas menampilkan pratinjau grafik kecepatan (Velocity Graph).

---

## 3. QuickJS Expression Authoring

```
[Inspector Property] ──► [Long-press Keyframe / Option] ──► [Edit Expression]
                                                                  │
                                                                  ▼
                                                      [Input JS Code via Chips]
                                                                  │
                                                                  ▼
                                                        [Syntax Check (OK)]
                                                           │           │
                                                          (No)       (Yes)
                                                           │           │
                                                           ▼           ▼
                                                   [Highlight Err] [Apply Run]
```

### Tahapan:
1. Membuka Editor: Bottom sheet expression muncul dengan kode template default: `value;`.
2. Penulisan Prosedural: Menggunakan chips bar untuk memasukkan variabel `time`, `sin()`, dsb.
3. Validasi: QuickJS runtime mengevaluasi ekspresi pada frame 0. Jika terjadi syntax error atau runtime crash, baris error diberi tanda peringatan merah dan tombol Apply dinonaktifkan.

---

## 4. Export & Rendering Workflow

```
[Tap Export] ──► [Review Presets] ──► [Select 1080p 60fps] ──► [Start Export]
                                                                     │
                                                                     ▼
                                                         [Progress Modal Display]
                                                                     │
                                                       ┌─────────────┴─────────────┐
                                                       ▼                           ▼
                                                [Success / Share]         [Storage Error / Retry]
```

### Error Recovery & Batas Aman:
- Error Kapasitas Penyimpanan: Pengecekan ukuran disk sebelum render. Jika ruang < estimasi file + buffer 100MB, dialog peringatan muncul sebelum proses dimulai.
- Thermal Throttling: Jika encoder mendeteksi hardware panas/dropped frame, framerate encode diturunkan secara gradual tanpa mengorbankan kualitas final frame.
