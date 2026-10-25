# OpenFlow UI/UX Design Package

Spesifikasi sistem desain dan antarmuka OpenFlow. Editor video dan motion graphics open-source berbasis Jetpack Compose dan Material 3 (M3).

## Indeks Berkas

```
openflow-ux/
├── 00-index.md                                    # Dokumen utama navigasi
├── 01-summary/
│   ├── design-specification.md                   # Layout 5 area utama editor dan wireframe
│   └── user-journey-map.md                       # Alur kerja editing, kurva, dan export
├── 02-analysis/
│   ├── design-system-tokens.md                   # Token M3 dark mode, warna netral D65, tipografi
│   └── accessibility-heuristics.md               # Evaluasi 48dp touch target, kontras, thumb zone
└── 03-dossiers/
    ├── component-specifications.md               # Komponen spesifik: timeline, kurva, inspector
    └── edge-cases-and-empty-states.md            # Status error, parsing ekspresi, zero states
```

## Prinsip Desain Inti
1. Netralitas Kromatik: Background dan container memakai grey scale murni tanpa tint biru/ungu agar tidak merusak kalibrasi warna video.
2. Densitas Informasi Pro: Rasio kontrol terhadap konten terukur, prioritas pada viewport preview dan manipulasi timeline presisi.
3. Ergonomi Mobile: Interaksi primer berada pada 60% area bawah layar (zona jempol).
4. Tanpa Dekorasi Berlebih: Tanpa efek blur kaca (glassmorphism), tanpa gradien pendar (glow). Murni berbasis kontras tonal Material 3.
