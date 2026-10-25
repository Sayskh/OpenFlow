# Edge Cases & Empty States: OpenFlow

Spesifikasi penanganan kondisi batas, galat komputasi, dan status tanpa data.

---

## 1. Timeline Empty State (Proyek Baru)

Kondisi: Pengguna membuat proyek baru dan belum ada berkas media yang dimasukkan ke timeline.

### Representasi Visual:
- Area Timeline: Tidak menampilkan track kosong berlapis.
- Komponen Tengah:
  - Ikon: Ikon M3 `movie_filter` atau `add_photo_alternate` ukuran 48 dp dalam warna `#525252`.
  - Teks Judul: `Belum Ada Media` (`titleMedium`, `#EEEEEE`).
  - Teks Penjelas: `Tekan tombol tambah atau seret video untuk mulai menyusun timeline.` (`bodySmall`, `#A3A3A3`).
  - Tombol Tindakan Primer: Tombol Tonal `+ Impor Video / Audio` (tinggi 44 dp, radius 12 dp).
- Area Viewport: Menampilkan outline rasio aspek yang dipilih (misal 16:9) dengan teks resolusi proyek `1080 x 1920 | 30.00 fps` di tengah layar.

---

## 2. Curve Editor Zero Keyframes State

Kondisi: Pengguna menekan ikon editor kurva pada properti yang belum memiliki minimal dua keyframe.

### Solusi Desain:
- Pesan Edukatif: Canvas kurva tetap ditampilkan dengan opasitas 30% bergaris putus-putus.
- Overlay Banner:
  - Teks: `Dibutuhkan minimal 2 keyframe untuk membuat kurva interpolasi.`
  - Tombol Otomatis: `+ Buat Keyframe Awal & Akhir`
  - Aksi: Menekan tombol akan membuat satu keyframe pada posisi playhead saat ini dan satu keyframe kedua 1 detik berikutnya dengan nilai sama sebagai titik awal manipulasi kurva.

---

## 3. QuickJS Expression Syntax Error & Crash Prevention

Kondisi: Pengguna mengetik ekspresi tak lengkap atau sintaks salah (contoh: tanda kurung terbuka, memanggil fungsi undefined).

### Solusi Desain:
- Evaluasi Sandbox: Skrip dievaluasi di worker thread dengan timeout eksekusi 50ms untuk mencegah UI thread freeze akibat infinite loop (`while(true)`).
- Penanda Baris: Baris yang mengalami error ditandai dengan ikon seru merah `#F87171` di gutter nomor baris.
- Status Bar:
  - Pesan Error: `SyntaxError: unexpected token '}' at line 3`
  - Tombol Revert: `Kembalikan Kode Terakhir yang Valid`
- Proteksi Render: Engine video editor otomatis mengabaikan ekspresi bermasalah dan kembali menggunakan nilai dasar properti (fallback to base value) sehingga viewport tidak hang atau crash.

---

## 4. Export Capacity & Thermal Edge Cases

### Kasus A: Ruang Penyimpanan Tidak Cukup
- Pemicu: Kapasitas memori tersisa < Estimasi ukuran berkas output + 200MB safety margin.
- Dialog Peringatan:
  - Judul: `Ruang Penyimpanan Rendah`
  - Deskripsi: `Dibutuhkan minimal 450 MB untuk merender video ini. Tersisa hanya 180 MB.`
  - Pilihan Tindakan:
    - `Ganti Preset (Ubah ke 720p)`
    - `Batal`

### Kasus B: Hardware Encoder Failure / Codec Unsupported
- Pemicu: Perangkat tidak mendukung profil hardware HEVC 4K 60fps.
- Solusi: Selektor pada Export Sheet secara proaktif mendisposisikan opsi yang tidak didukung menjadi disable (abu-abu `#525252`) dengan label kecil `Tak didukung hardware`.
- Fallback: Secara otomatis mengarahkan opsi default ke `H.264 (AVC) 1080p 30fps`.

### Kasus C: Teks Truncation & String Panjang
- Nama Klip Panjang: Klip berdurasi pendek dengan nama file panjang (contoh: `VID_20261025_LONG_FILE_NAME.mp4`) dipotong menggunakan pola elipsis tengah: `VID_2026...NAME.mp4`.
- Durasi Klip Sangat Pendek (< 3 frame): Klip diperluas dengan minimum visual width 24 dp pada level zoom apa pun agar tetap dapat disentuh jari.
