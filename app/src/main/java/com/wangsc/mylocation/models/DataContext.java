package com.wangsc.mylocation.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangsc.mylocation.utils._Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;
    private Context context;

    public DataContext(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    //region Location

    public void addLocation(Location model) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("Id", model.Id.toString());
            values.put("UserId", model.UserId.toString());
            values.put("LocationType", model.LocationType);
            values.put("Longitude", model.Longitude);
            values.put("Latitude", model.Latitude);
            values.put("Accuracy", model.Accuracy);
            values.put("Provider", model.Provider);
            values.put("Speed", model.Speed);
            values.put("Bearing", model.Bearing);
            values.put("Satellites", model.Satellites);
            values.put("Country", model.Country);
            values.put("Province", model.Province);
            values.put("City", model.City);
            values.put("CityCode", model.CityCode);
            values.put("District", model.District);
            values.put("AdCode", model.AdCode);
            values.put("Address", model.Address);
            values.put("PoiName", model.PoiName);
            values.put("Time", model.Time);
            values.put("Summary", model.Summary);
            //调用方法插入数据
            db.insert("location", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public List<Location> getLocatios(UUID UserId, boolean isTimeDesc) {
        List<Location> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("location", null, "UserId=?", new String[]{UserId.toString() + ""}, null, null, isTimeDesc ? "time DESC" : "time ASC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                Location model = new Location();
                model.Id = UUID.fromString(cursor.getString(0));
                model.UserId = UUID.fromString(cursor.getString(1));
                model.LocationType = cursor.getInt(2);
                model.Longitude = cursor.getDouble(3);
                model.Latitude = cursor.getDouble(4);
                model.Accuracy = cursor.getFloat(5);
                model.Provider = cursor.getString(6);
                model.Speed = cursor.getFloat(7);
                model.Bearing = cursor.getFloat(8);
                model.Satellites = cursor.getInt(9);
                model.Country = cursor.getString(10);
                model.Province = cursor.getString(11);
                model.City = cursor.getString(12);
                model.CityCode = cursor.getString(13);
                model.District = cursor.getString(14);
                model.AdCode = cursor.getString(15);
                model.Address = cursor.getString(16);
                model.PoiName = cursor.getString(17);
                model.Time = cursor.getLong(18);
                model.Summary = cursor.getString(19);
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public Location getLatestLocatio(UUID UserId) {
        Location model = new Location();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("location", null, "UserId=?", new String[]{UserId.toString() + ""}, null, null, "time DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                model.Id = UUID.fromString(cursor.getString(0));
                model.UserId = UUID.fromString(cursor.getString(1));
                model.LocationType = cursor.getInt(2);
                model.Longitude = cursor.getDouble(3);
                model.Latitude = cursor.getDouble(4);
                model.Accuracy = cursor.getFloat(5);
                model.Provider = cursor.getString(6);
                model.Speed = cursor.getFloat(7);
                model.Bearing = cursor.getFloat(8);
                model.Satellites = cursor.getInt(9);
                model.Country = cursor.getString(10);
                model.Province = cursor.getString(11);
                model.City = cursor.getString(12);
                model.CityCode = cursor.getString(13);
                model.District = cursor.getString(14);
                model.AdCode = cursor.getString(15);
                model.Address = cursor.getString(16);
                model.PoiName = cursor.getString(17);
                model.Time = cursor.getLong(18);
                model.Summary = cursor.getString(19);
                break;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return model;
    }

    public List<Location> getLocatiosByDayspan(UUID UserId, int daySpan, boolean isTimeDesc) {
        List<Location> result = new ArrayList<>();
        try {
            long now = System.currentTimeMillis();
            long pre = new DateTime().addDays(-daySpan).getTimeInMillis();
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("location", null, "UserId=? AND Time>? AND Time<?", new String[]{UserId.toString() + "", pre + "", now + ""}, null, null, isTimeDesc ? "time DESC" : null);
            while (cursor.moveToNext()) {
                Location model = new Location();
                model.Id = UUID.fromString(cursor.getString(0));
                model.UserId = UUID.fromString(cursor.getString(1));
                model.LocationType = cursor.getInt(2);
                model.Longitude = cursor.getDouble(3);
                model.Latitude = cursor.getDouble(4);
                model.Accuracy = cursor.getFloat(5);
                model.Provider = cursor.getString(6);
                model.Speed = cursor.getFloat(7);
                model.Bearing = cursor.getFloat(8);
                model.Satellites = cursor.getInt(9);
                model.Country = cursor.getString(10);
                model.Province = cursor.getString(11);
                model.City = cursor.getString(12);
                model.CityCode = cursor.getString(13);
                model.District = cursor.getString(14);
                model.AdCode = cursor.getString(15);
                model.Address = cursor.getString(16);
                model.PoiName = cursor.getString(17);
                model.Time = cursor.getLong(18);
                model.Summary = cursor.getString(19);
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<Location> getTodayLocatios(UUID UserId) {
        List<Location> result = new ArrayList<>();
        DateTime today = new DateTime();
        DateTime start = new DateTime(today.getYear(), today.getMonth(), today.getDay());
        DateTime end = start.addDays(1);
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("location", null, "UserId=? AND Time>? AND Time<?", new String[]{UserId.toString(), start.getTimeInMillis() + "", end.getTimeInMillis() + ""}, null, null, null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                Location model = new Location();
                model.Id = UUID.fromString(cursor.getString(0));
                model.UserId = UUID.fromString(cursor.getString(1));
                model.LocationType = cursor.getInt(2);
                model.Longitude = cursor.getDouble(3);
                model.Latitude = cursor.getDouble(4);
                model.Accuracy = cursor.getFloat(5);
                model.Provider = cursor.getString(6);
                model.Speed = cursor.getFloat(7);
                model.Bearing = cursor.getFloat(8);
                model.Satellites = cursor.getInt(9);
                model.Country = cursor.getString(10);
                model.Province = cursor.getString(11);
                model.City = cursor.getString(12);
                model.CityCode = cursor.getString(13);
                model.District = cursor.getString(14);
                model.AdCode = cursor.getString(15);
                model.Address = cursor.getString(16);
                model.PoiName = cursor.getString(17);
                model.Time = cursor.getLong(18);
                model.Summary = cursor.getString(19);
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public void clearLocations(int accuracy) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("location", "accuracy>?", new String[]{accuracy + ""});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteLocation(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("location", "id=?", new String[]{id + ""});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    //endregion

    //region RunLog
    public List<RunLog> getRunLogs() {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, null, null, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    @NotNull
    public List<RunLog> getRunLogsByTag(String tag) {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, "tag like ?", new String[]{tag}, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByEquals(String item) {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, "item like ?", new String[]{item}, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByLike(String[] itemLike) {
        List<RunLog> result = new ArrayList<>();
        String where = "";
        for (int i = 0; i < itemLike.length; i++) {
            where += " item like  ? ";
            if (i < itemLike.length - 1) {
                where += "OR";
            }
        }
        String[] whereArg = new String[itemLike.length];
        for (int i = 0; i < itemLike.length; i++) {
            whereArg[i] = "%" + itemLike[i] + "%";
        }

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, where, whereArg, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public void addLog(String tag, String item, String message){
        RunLog runLog = new RunLog(UUID.randomUUID());
        runLog.setTag(tag);
        runLog.setItem(item);
        runLog.setMessage(message);
        runLog.setRunTime(new DateTime());
        addRunLog(runLog);
    }

    public void addRunLog(RunLog runLog) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", runLog.getId().toString());
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void addRunLog(String item, String message) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", UUID.randomUUID().toString());
            values.put("runTime", System.currentTimeMillis());
            values.put("tag", new DateTime(System.currentTimeMillis()).toLongDateTimeString());
            values.put("item", item);
            values.put("message", message);
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void updateRunLog(RunLog runLog) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());

            db.update("runLog", values, "id=?", new String[]{runLog.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void clearRunLog() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", null, null);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    public void clearRunLogByTag(String tag) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "tag like ?", new String[]{tag});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByEquals(String item) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + item + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByLike(String itemLike) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + itemLike + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLog(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "id = ?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region Setting
    public Setting getSetting(Object name) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, "name=?", new String[]{name.toString()}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(name.toString(), cursor.getString(1), cursor.getInt(2));
            cursor.close();
            db.close();
            return setting;
        }
        return null;
    }

    public Setting getSetting(Object name, Object defaultValue) {
        Setting setting = getSetting(name);
        if (setting == null) {
            this.addSetting(name, defaultValue);
            setting = new Setting(name.toString(), defaultValue.toString(), 100);
            return setting;
        }
        return setting;
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     *
     * @param name
     * @param value
     */
    public void editSetting(Object name, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("value", value.toString());
        if (db.update("setting", values, "name=?", new String[]{name.toString()}) == 0) {
            this.addSetting(name, value.toString());
        }
        db.close();
    }

    public void editSettingLevel(Object name, int level) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("level", level + "");
        db.update("setting", values, "name=?", new String[]{name.toString()});
        db.close();
    }

    public void deleteSetting(Object name) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "name=?", new String[]{name.toString()});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void deleteSetting(String name) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "name=?", new String[]{name});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void addSetting(Object name, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("name", name.toString());
        values.put("value", value.toString());
        //调用方法插入数据
        db.insert("setting", "name", values);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public List<Setting> getSettings() {
        List<Setting> result = new ArrayList<>();
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, null, null, null, null, "level,name");
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
            result.add(setting);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearSetting() {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", null, null);
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    //endregion
}
