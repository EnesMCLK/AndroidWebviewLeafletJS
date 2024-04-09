package com.example.tezuygulamasi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context mContext;
    private static final String DATABASE_TABLE_MARKER = "marker";
    private static final String DATABASE_NAME = "dbtesarz.db";
    private static final String DATABASE_PATH = "data/" + DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bu metod sadece boş bir veritabanı dosyası oluşturur,
        // çünkü veritabanı dosyasını manuel olarak ekledik.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Gerekirse, veritabanınızı güncellemek için bu metod kullanılabilir.
    }

    public void copyDatabase() throws IOException {
        // Veritabanı zaten varsa kopyalamayı atla
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        if (dbFile.exists()) return;

        // Assets'ten veritabanını kopyala
        InputStream is = mContext.getAssets().open(DATABASE_PATH);
        OutputStream os = Files.newOutputStream(dbFile.toPath());

        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        // Akışları kapat
        os.flush();
        os.close();
        is.close();
    }

    public String getData() {
        /*
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ DATABASE_TABLE_MARKER + " WHERE SiraNo = '1425'", null);
        StringBuilder stringBuilder = new StringBuilder();

        while (cursor.moveToNext()) {
            // İlk sütunun değerini almak için
            String data = cursor.getString(2); // Sütun indeksi, sorgunuzdaki sütunlara bağlı olarak değişebilir
            stringBuilder.append(data).append("\n");
        }

        // Burada örnek bir sorgu yaptık veritabanına girip 1425 SiraNo isimli sütundan 2. indisli elemanı çekip
        // Toast mesajı ile ekranda gösterdik.


        cursor.close();
        db.close();
        return stringBuilder.toString();

         */
        return null;
    }


}


