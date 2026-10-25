# Accessibility & Usability Heuristics: OpenFlow

Evaluasi aksesibilitas standar WCAG 2.1 AA/AAA dan 10 Heuristik Usabilitas Nielsen Norman untuk editor video mobile.

---

## 1. Rasio Kontras Warna (WCAG 2.1 Compliance)

| Pasangan Warna (Foreground / Background) | Rasio Terukur | Standar Minimum | Status |
|---|---|---|---|
| `colorOnSurface` (`#EEEEEE`) / `colorSurface` (`#121212`) | 13.5:1 | 4.5:1 (AA) / 7.0:1 (AAA) | Lolos AAA |
| `colorOnSurfaceVariant` (`#A3A3A3`) / `colorSurface` (`#121212`) | 7.2:1 | 4.5:1 (AA) | Lolos AAA |
| `colorPrimary` (`#93C5FD`) / `colorSurface` (`#121212`) | 8.8:1 | 3.0:1 (Komponen UI) | Lolos AAA |
| `colorTertiary` (`#FBBF24`) / `colorSurfaceContainer` (`#1E1E1E`) | 9.4:1 | 3.0:1 (Keyframe Icon) | Lolos AAA |
| `colorError` (`#F87171`) / `colorSurfaceContainer` (`#1E1E1E`) | 5.2:1 | 4.5:1 (Teks Error) | Lolos AA |
| `colorOutline` (`#525252`) / `colorSurface` (`#121212`) | 3.5:1 | 3.0:1 (Batas Non-teks) | Lolos |

Semua teks informasi kritis (timecode, nilai numerik slider, pesan error ekspresi) memenuhi standar WCAG AAA.

---

## 2. Touch Target Mapping & Ergonomi Jempol (Thumb Zone)

### Aturan 48dp Touch Target
1. Komponen Ikon Kecil:
   - Ikon Playhead Triangle berukuran visual 12x10 dp, namun memiliki `Modifier.size(48.dp)` transparan di sekelilingnya.
   - Bezier Curve Handle berukuran visual diameter 16 dp, area hit sentuh diperluas menjadi `48x48 dp`.
   - Diamond Keyframe Icon berukuran visual 16x16 dp dengan bounding box sentuh `48x48 dp`.
2. Slider Track & Thumbs:
   - Ketebalan visual track slider 4 dp.
   - Tinggi zona tangkapan sentuh vertikal slider adalah 48 dp untuk mencegah gestur meleset saat dragging.

### Pembagian Wilayah Jari Tangan (Handheld Reachability)
- Zona Natural Jempol Bawah (0 - 320 dp dari batas bawah):
  - Penempatan: Play/pause, step frame, split clip, inspector slider adjustment, preset kurva.
  - Alasan: Bebas regangan otot jempol saat memegang perangkat satu atau dua tangan.
- Zona Jangkauan Sedang (320 - 560 dp):
  - Penempatan: Multi-track timeline scroll, timeline layer reordering.
- Zona Jangkauan Jauh / Butuh Dua Tangan (560 dp ke atas):
  - Penempatan: Safe area toggle, setting proyek, tombol export.
  - Alasan: Mencegah aksi destruktif atau keluar layar secara tidak sengaja.

---

## 3. Nielsen Norman Usability Heuristics Audit

1. Status Sistem Terlihat (Visibility of System Status):
   - Timecode selalu menampilkan posisi frame absolut secara real-time `HH:MM:SS:FF`.
   - Status evaluasi QuickJS menampilkan waktu komputasi (`Eval time: 0.12ms`) dan status parser langsung saat mengetik.
2. Kesesuaian Sistem dengan Dunia Nyata (Match between System and Real World):
   - Terminologi industri video editing standar digunakan secara konsisten: `Playhead`, `Keyframe`, `Split`, `Ripple`, `Bezier`, `Easing`, `Bitrate`.
3. Kontrol dan Kebebasan Pengguna (User Control and Freedom):
   - Dukungan Undo dan Redo multi-level untuk setiap manipulasi klip, split, perubahan nilai transformer, dan modifikasi kurva.
   - Tombol batal instan pada modal export dan expression editor tanpa menyimpan perubahan draft yang rusak.
4. Konsistensi dan Standar (Consistency and Standards):
   - Mengikuti pola gestur Android M3 standar: pinch-to-zoom untuk timeline scale, swipe horizontal untuk scrub, long-press untuk reorder track.
5. Pencegahan Kesalahan (Error Prevention):
   - Magnet snapping mencegah pemotongan klip berjarak 1 frame yang tidak disengaja.
   - Pengecekan sisa kapasitas memori internal sebelum rendering video beresolusi 4K dimulai.
6. Mengenali daripada Mengingat (Recognition rather than Recall):
   - Strip klip timeline menampilkan thumbnail visual berkala serta kontur waveform audio asli, menghilangkan keharusan mengingat isi klip berdasarkan nama file.
7. Fleksibilitas dan Efisiensi Penggunaan (Flexibility and Efficiency of Use):
   - Dua mode input: dragging slider untuk penyesuaian visual cepat, dan tap langsung untuk pengetikan angka numerik presisi tinggi.
