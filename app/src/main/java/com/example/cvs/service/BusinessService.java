package com.example.cvs.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;

import com.example.cvs.dto.BrandDto;
import com.example.cvs.dto.FunctionDto;
import com.example.cvs.util.DbUtil;
import com.example.cvs.util.VersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 12/25/16.
 */

public class BusinessService {

    public Context context;

    DbUtil db;

    public int countByFunctionId(int functionId) throws PackageManager.NameNotFoundException {
        db = new DbUtil(this.context, VersionUtils.getVersionCode(this.context));
        Cursor cursor = db.getAll("select id from tbl_brand where functionId=" + functionId);
        return cursor.getCount();
    }

    public List<BrandDto> loadBrandsByFunctionId(int functionId) throws PackageManager.NameNotFoundException {
        db = new DbUtil(this.context, VersionUtils.getVersionCode(this.context));
        List<BrandDto> res = new ArrayList<BrandDto>();

        Cursor cursor = db.getAll("select brand, generic from tbl_brand where functionId=" + functionId);

        while (cursor.isAfterLast() == false) {
            BrandDto dto = new BrandDto(cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("generic")), functionId);
            res.add(dto);
            cursor.moveToNext();
        }

        return res;
    }

    public List<FunctionDto> loadFunctionList() throws PackageManager.NameNotFoundException {
        db = new DbUtil(this.context, VersionUtils.getVersionCode(this.context));
        List<FunctionDto> res = new ArrayList<FunctionDto>();

        Cursor cursor = db.getAll("select id, description from tbl_function");

        while (cursor.isAfterLast() == false) {
            FunctionDto dto = new FunctionDto(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("description")));
            res.add(dto);
            cursor.moveToNext();
        }

        return res;
    }

    public void saveFunction(FunctionDto dto) throws PackageManager.NameNotFoundException {
        db = new DbUtil(this.context, VersionUtils.getVersionCode(this.context));
        ContentValues values = new ContentValues();
        values.put("description", dto.getDescription());
        db.insert("tbl_function", values);
    }

    public void saveBrand(BrandDto dto) throws PackageManager.NameNotFoundException {
        db = new DbUtil(this.context, VersionUtils.getVersionCode(this.context));
        ContentValues values = new ContentValues();
        values.put("brand", dto.getBrand());
        values.put("generic", dto.getGeneric());
        values.put("functionId", dto.getFunctionId());

        db.insert("tbl_brand", values);
    }
}
