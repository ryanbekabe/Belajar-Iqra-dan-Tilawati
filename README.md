# Belajar Iqra dan Tilawati

Aplikasi Android sederhana untuk anak-anak Muslim belajar membaca huruf Arab.

## Fitur Utama
- Mode `Huruf Hijaiyah`: tampil 30 huruf + transliterasi latin.
- Tombol `Dengarkan`: pelafalan huruf dengan Text-to-Speech (Arab).
- Navigasi `Sebelumnya` / `Berikutnya`.
- Mode `Iqra 1`: menampilkan materi halaman berbasis gambar.

## Iqra 1 Otomatis (Tanpa Edit Kode)
Aplikasi akan memuat halaman Iqra otomatis dari drawable dengan pola nama:
- `iqra1_halaman_1`
- `iqra1_halaman_2`
- `iqra1_halaman_3`
- dst.

Contoh file:
- `app/src/main/res/drawable/iqra1_halaman_1.png`

Jika Anda menambah file dengan pola yang sama, halaman baru langsung terbaca otomatis saat aplikasi dijalankan.

## Build
```bash
./gradlew :app:assembleDebug
```

Di Windows (PowerShell/CMD):
```bat
gradlew.bat :app:assembleDebug
```

## Lokasi File Utama
- `app/src/main/java/com/hanyajasa/belajariqradantilawati/MainActivity.kt`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/values/strings.xml`
