package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.santteam.apphenhosinhvien.model.SoThich;

import java.util.ArrayList;

public class LocActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button btnXoaLoc;
    private CheckBox chkToanHoc, chkMiThuat, chkAmNhac, chkDuLich, chkTheThao;
    private ArrayList<String> soThichs = new ArrayList<>();
    private RadioButton radNam, radNu, radTatCa;
    private EditText edtTuoi, edtKhac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);
        addControls();
        toolbar.setTitle("Lọc Tìm Kiếm");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(SoThich.Intance().getSoThichs().size() > 0)
        {
            chkToanHoc.setChecked(SoThich.Intance().getSoThichs().get(0));
            chkDuLich.setChecked(SoThich.Intance().getSoThichs().get(1));
            chkAmNhac.setChecked(SoThich.Intance().getSoThichs().get(2));
            chkMiThuat.setChecked(SoThich.Intance().getSoThichs().get(3));
            chkTheThao.setChecked(SoThich.Intance().getSoThichs().get(4));
        }

        addEvents();
    }

    private void addEvents() {
        btnXoaLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkToanHoc.setChecked(false);
                chkAmNhac.setChecked(false);
                chkDuLich.setChecked(false);
                chkMiThuat.setChecked(false);
                chkTheThao.setChecked(false);
                radNam.setChecked(false);
                radNu.setChecked(false);
                radTatCa.setChecked(false);
                edtTuoi.setText("");
                edtKhac.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Xử lý checkbox Sở thích
        if(item.getItemId() == android.R.id.home){
            soThichs.clear();
            SoThich.Intance().getSoThichs().clear();
            if(chkToanHoc.isChecked() == true){
                soThichs.add(chkToanHoc.getText().toString());
            }
            else{
                soThichs.remove(chkToanHoc.getText().toString());
            }
            if(chkDuLich.isChecked() == true){
                soThichs.add(chkDuLich.getText().toString());
            }
            else{
                soThichs.remove(chkDuLich.getText().toString());
            }
            if(chkAmNhac.isChecked() == true){
                soThichs.add(chkAmNhac.getText().toString());
            }
            else{
                soThichs.remove(chkAmNhac.getText().toString());
            }
            if(chkMiThuat.isChecked() == true){
                soThichs.add(chkMiThuat.getText().toString());
            }
            else{
                soThichs.remove(chkMiThuat.getText().toString());
            }
            if(chkTheThao.isChecked() == true){
                soThichs.add(chkTheThao.getText().toString());
            }
            else{
                soThichs.remove(chkTheThao.getText().toString());
            }
            //Xử lý edittext Khác của sở thích.
            String[] soThichKhac = edtKhac.getText().toString().trim().split(",");
            for(String st: soThichKhac){
                soThichs.add(st);
            }

            //Xử lý RadioButton Giới tính
            String gioiTinh = "";
            if(radNam.isChecked()) gioiTinh += ""+radNam.getText().toString();
            if(radNu.isChecked()) gioiTinh += ""+radNu.getText().toString();
            if(radTatCa.isChecked()) gioiTinh += "Nam Nữ";
            if(gioiTinh.compareTo("") == 0) gioiTinh = null;
            //Xử lý edittext Tuổi.
            String tuoi = edtTuoi.getText().toString();
            if(tuoi.compareTo("") == 0 || (tuoi.compareTo("18") >= 0 && tuoi.compareTo("30") <= 0)) {
                SoThich.Intance().getSoThichs().add(chkToanHoc.isChecked());
                SoThich.Intance().getSoThichs().add(chkDuLich.isChecked());
                SoThich.Intance().getSoThichs().add(chkAmNhac.isChecked());
                SoThich.Intance().getSoThichs().add(chkMiThuat.isChecked());
                SoThich.Intance().getSoThichs().add(chkTheThao.isChecked());

                if (tuoi.compareTo("") == 0) tuoi = null;
                Intent intent = new Intent(this,TimKiemActivity.class);
                intent.putExtra("sothich",soThichs);
                intent.putExtra("gioitinh",gioiTinh);
                intent.putExtra("tuoi",tuoi);
                startActivity(intent);
                finish();
            }
            else Toast.makeText(this, "Tuổi bạn nhập không phù hợp !!(tuổi từ 18-30) và phải là chử số", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        btnXoaLoc = (Button) findViewById(R.id.btnXoaLoc);
        chkToanHoc = (CheckBox) findViewById(R.id.chkToanHoc);
        chkDuLich = (CheckBox) findViewById(R.id.chkDuLich);
        chkAmNhac = (CheckBox) findViewById(R.id.chkAmNhac);
        chkMiThuat = (CheckBox) findViewById(R.id.chkMiThuat);
        chkTheThao = (CheckBox) findViewById(R.id.chkTheThao);

        radNam = findViewById(R.id.rbNam);
        radNu = findViewById(R.id.rbNu);
        radTatCa = findViewById(R.id.rbTatCa);

        edtTuoi = findViewById(R.id.edtTuoi);
        edtKhac = findViewById(R.id.edtKhac);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        soThichs.clear();
        SoThich.Intance().getSoThichs().clear();
        if(chkToanHoc.isChecked() == true){
            soThichs.add(chkToanHoc.getText().toString());
        }
        else{
            soThichs.remove(chkToanHoc.getText().toString());
        }
        if(chkDuLich.isChecked() == true){
            soThichs.add(chkDuLich.getText().toString());
        }
        else{
            soThichs.remove(chkDuLich.getText().toString());
        }
        if(chkAmNhac.isChecked() == true){
            soThichs.add(chkAmNhac.getText().toString());
        }
        else{
            soThichs.remove(chkAmNhac.getText().toString());
        }
        if(chkMiThuat.isChecked() == true){
            soThichs.add(chkMiThuat.getText().toString());
        }
        else{
            soThichs.remove(chkMiThuat.getText().toString());
        }
        if(chkTheThao.isChecked() == true){
            soThichs.add(chkTheThao.getText().toString());
        }
        else{
            soThichs.remove(chkTheThao.getText().toString());
        }
        //Xử lý edittext Khác của sở thích.
        String[] soThichKhac = edtKhac.getText().toString().trim().split(",");
        for(String st: soThichKhac){
            soThichs.add(st);
        }

        //Xử lý RadioButton Giới tính
        String gioiTinh = "";
        if(radNam.isChecked()) gioiTinh += ""+radNam.getText().toString();
        if(radNu.isChecked()) gioiTinh += ""+radNu.getText().toString();
        if(radTatCa.isChecked()) gioiTinh += "Nam Nữ";
        if(gioiTinh.compareTo("") == 0) gioiTinh = null;
        //Xử lý edittext Tuổi.
        String tuoi = edtTuoi.getText().toString();
        if(tuoi.compareTo("") == 0 || (tuoi.compareTo("18") >= 0 && tuoi.compareTo("30") <= 0)) {
            SoThich.Intance().getSoThichs().add(chkToanHoc.isChecked());
            SoThich.Intance().getSoThichs().add(chkDuLich.isChecked());
            SoThich.Intance().getSoThichs().add(chkAmNhac.isChecked());
            SoThich.Intance().getSoThichs().add(chkMiThuat.isChecked());
            SoThich.Intance().getSoThichs().add(chkTheThao.isChecked());

            if (tuoi.compareTo("") == 0) tuoi = null;
            Intent intent = new Intent(this,TimKiemActivity.class);
            intent.putExtra("sothich",soThichs);
            intent.putExtra("gioitinh",gioiTinh);
            intent.putExtra("tuoi",tuoi);
            startActivity(intent);
            finish();
        }
        else Toast.makeText(this, "Tuổi bạn nhập không phù hợp !!(tuổi từ 18-30) và phải là chử số", Toast.LENGTH_LONG).show();

    }
}
