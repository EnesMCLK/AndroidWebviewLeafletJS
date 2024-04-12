package com.example.tezuygulamasi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context mContext;
    private static final String DATABASE_TABLE_MARKER = "marker";
    private static final String DATABASE_NAME = "dbtesarz.db";
    private static final String DATABASE_PATH = "data/" + DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;
    private final int[] same = {0,1,2,3,4,5,6,7};
    private String data;
    private List<String> listData,listIstasyonNo,listBaslik,listMarka,listHSekli,listIsletmeci,listYesilSarj,listAdres,listSoketNo,listSoketTipi,listSoketTuru,listSoketGucu,listX,listY;
    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected StringBuilder stringBuilder;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Bu metod sadece boş bir veritabanı dosyası oluşturur
           ama biz veritabanı dosyasını manuel olarak ekledik.  */
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

    public void readDatabase(String siraNo){
        db = this.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM "+ DATABASE_TABLE_MARKER +" WHERE SiraNo = "+ siraNo, null);
    }

    public List<String> insertLoopCloseDB(List<String> list, int sutunNo){
        stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            data = cursor.getString(Integer.parseInt(Integer.toString(sutunNo)));
            stringBuilder.append(data).append("\n");
            list.add(data);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getData(String siraNo, int sutunNo) {
        readDatabase(siraNo);
        listData = new ArrayList<>();
        return listData=insertLoopCloseDB(listData,sutunNo);
    }

    public List<String> getIstasyonNo(String siraNo){
        readDatabase(siraNo);
        listIstasyonNo = new ArrayList<>();
        return listIstasyonNo=insertLoopCloseDB(listIstasyonNo,1);
    }

    public List<String> getBaslik(String siraNo){
        readDatabase(siraNo);
        listBaslik = new ArrayList<>();
        return listBaslik=insertLoopCloseDB(listBaslik,2);
    }

    public List<String> getHizmetSekli(String siraNo){
        readDatabase(siraNo);
        listHSekli = new ArrayList<>();
        return listHSekli=insertLoopCloseDB(listHSekli,3);
    }

    public List<String> getMarka(String siraNo){
        readDatabase(siraNo);
        listMarka = new ArrayList<>();
        return listMarka=insertLoopCloseDB(listMarka,4);
    }

    public List<String> getSarjAgiIsletmecisi(String siraNo){
        readDatabase(siraNo);
        listIsletmeci = new ArrayList<>();
        return listIsletmeci=insertLoopCloseDB(listIsletmeci,5);
    }

    public List<String> getYesilSarj(String siraNo){
        readDatabase(siraNo);
        listYesilSarj = new ArrayList<>();
        return listYesilSarj=insertLoopCloseDB(listYesilSarj,6);
    }

    public List<String> getAdres(String siraNo){
        readDatabase(siraNo);
        listAdres = new ArrayList<>();
        return listAdres=insertLoopCloseDB(listAdres,7);
    }

    public List<String> getSoketNo(String siraNo){
        readDatabase(siraNo);
        listSoketNo = new ArrayList<>();
        return listSoketNo=insertLoopCloseDB(listSoketNo,7);
    }

    public List<String> getSoketTipi(String siraNo){
        readDatabase(siraNo);
        listSoketTipi = new ArrayList<>();
        return listSoketTipi=insertLoopCloseDB(listSoketTipi,8);
    }

    public List<String> getSoketTuru(String siraNo){
        readDatabase(siraNo);
        listSoketTuru = new ArrayList<>();
        return listSoketTuru=insertLoopCloseDB(listSoketTuru,8);
    }

    public List<String> getSoketGucu(String siraNo){
        readDatabase(siraNo);
        listSoketGucu = new ArrayList<>();
        return listSoketGucu=insertLoopCloseDB(listSoketGucu,9);
    }

    public List<String> getX(String siraNo){
        readDatabase(siraNo);
        listX = new ArrayList<>();
        return listX=insertLoopCloseDB(listX,10);
    }

    public List<String> getY(String siraNo){
        readDatabase(siraNo);
        listY = new ArrayList<>();
        return listY=insertLoopCloseDB(listY,11);
    }
}