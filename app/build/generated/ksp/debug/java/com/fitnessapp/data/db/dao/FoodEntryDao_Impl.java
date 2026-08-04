package com.fitnessapp.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fitnessapp.data.db.entity.FoodEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FoodEntryDao_Impl implements FoodEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FoodEntry> __insertionAdapterOfFoodEntry;

  private final EntityDeletionOrUpdateAdapter<FoodEntry> __deletionAdapterOfFoodEntry;

  private final EntityDeletionOrUpdateAdapter<FoodEntry> __updateAdapterOfFoodEntry;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public FoodEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFoodEntry = new EntityInsertionAdapter<FoodEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `food_entries` (`id`,`name`,`calories`,`proteinGrams`,`carbsGrams`,`fatGrams`,`fiberGrams`,`sugarGrams`,`sodiumMg`,`cholesterolMg`,`mealType`,`dateMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCalories());
        statement.bindDouble(4, entity.getProteinGrams());
        statement.bindDouble(5, entity.getCarbsGrams());
        statement.bindDouble(6, entity.getFatGrams());
        statement.bindDouble(7, entity.getFiberGrams());
        statement.bindDouble(8, entity.getSugarGrams());
        statement.bindDouble(9, entity.getSodiumMg());
        statement.bindDouble(10, entity.getCholesterolMg());
        statement.bindString(11, entity.getMealType());
        statement.bindLong(12, entity.getDateMillis());
      }
    };
    this.__deletionAdapterOfFoodEntry = new EntityDeletionOrUpdateAdapter<FoodEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `food_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFoodEntry = new EntityDeletionOrUpdateAdapter<FoodEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `food_entries` SET `id` = ?,`name` = ?,`calories` = ?,`proteinGrams` = ?,`carbsGrams` = ?,`fatGrams` = ?,`fiberGrams` = ?,`sugarGrams` = ?,`sodiumMg` = ?,`cholesterolMg` = ?,`mealType` = ?,`dateMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCalories());
        statement.bindDouble(4, entity.getProteinGrams());
        statement.bindDouble(5, entity.getCarbsGrams());
        statement.bindDouble(6, entity.getFatGrams());
        statement.bindDouble(7, entity.getFiberGrams());
        statement.bindDouble(8, entity.getSugarGrams());
        statement.bindDouble(9, entity.getSodiumMg());
        statement.bindDouble(10, entity.getCholesterolMg());
        statement.bindString(11, entity.getMealType());
        statement.bindLong(12, entity.getDateMillis());
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM food_entries";
        return _query;
      }
    };
  }

  @Override
  public long insert(final FoodEntry entry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfFoodEntry.insertAndReturnId(entry);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final FoodEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __deletionAdapterOfFoodEntry.handle(entry);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final FoodEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __updateAdapterOfFoodEntry.handle(entry);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAll.release(_stmt);
    }
  }

  @Override
  public Flow<List<FoodEntry>> getEntriesForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT * FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ? ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<List<FoodEntry>>() {
      @Override
      @NonNull
      public List<FoodEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProteinGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinGrams");
          final int _cursorIndexOfCarbsGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsGrams");
          final int _cursorIndexOfFatGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fatGrams");
          final int _cursorIndexOfFiberGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberGrams");
          final int _cursorIndexOfSugarGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarGrams");
          final int _cursorIndexOfSodiumMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumMg");
          final int _cursorIndexOfCholesterolMg = CursorUtil.getColumnIndexOrThrow(_cursor, "cholesterolMg");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final List<FoodEntry> _result = new ArrayList<FoodEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpCalories;
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories);
            final float _tmpProteinGrams;
            _tmpProteinGrams = _cursor.getFloat(_cursorIndexOfProteinGrams);
            final float _tmpCarbsGrams;
            _tmpCarbsGrams = _cursor.getFloat(_cursorIndexOfCarbsGrams);
            final float _tmpFatGrams;
            _tmpFatGrams = _cursor.getFloat(_cursorIndexOfFatGrams);
            final float _tmpFiberGrams;
            _tmpFiberGrams = _cursor.getFloat(_cursorIndexOfFiberGrams);
            final float _tmpSugarGrams;
            _tmpSugarGrams = _cursor.getFloat(_cursorIndexOfSugarGrams);
            final float _tmpSodiumMg;
            _tmpSodiumMg = _cursor.getFloat(_cursorIndexOfSodiumMg);
            final float _tmpCholesterolMg;
            _tmpCholesterolMg = _cursor.getFloat(_cursorIndexOfCholesterolMg);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _item = new FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProteinGrams,_tmpCarbsGrams,_tmpFatGrams,_tmpFiberGrams,_tmpSugarGrams,_tmpSodiumMg,_tmpCholesterolMg,_tmpMealType,_tmpDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<FoodEntry>> getAllEntries() {
    final String _sql = "SELECT * FROM food_entries ORDER BY dateMillis DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<List<FoodEntry>>() {
      @Override
      @NonNull
      public List<FoodEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProteinGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinGrams");
          final int _cursorIndexOfCarbsGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsGrams");
          final int _cursorIndexOfFatGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fatGrams");
          final int _cursorIndexOfFiberGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberGrams");
          final int _cursorIndexOfSugarGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarGrams");
          final int _cursorIndexOfSodiumMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumMg");
          final int _cursorIndexOfCholesterolMg = CursorUtil.getColumnIndexOrThrow(_cursor, "cholesterolMg");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final List<FoodEntry> _result = new ArrayList<FoodEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpCalories;
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories);
            final float _tmpProteinGrams;
            _tmpProteinGrams = _cursor.getFloat(_cursorIndexOfProteinGrams);
            final float _tmpCarbsGrams;
            _tmpCarbsGrams = _cursor.getFloat(_cursorIndexOfCarbsGrams);
            final float _tmpFatGrams;
            _tmpFatGrams = _cursor.getFloat(_cursorIndexOfFatGrams);
            final float _tmpFiberGrams;
            _tmpFiberGrams = _cursor.getFloat(_cursorIndexOfFiberGrams);
            final float _tmpSugarGrams;
            _tmpSugarGrams = _cursor.getFloat(_cursorIndexOfSugarGrams);
            final float _tmpSodiumMg;
            _tmpSodiumMg = _cursor.getFloat(_cursorIndexOfSodiumMg);
            final float _tmpCholesterolMg;
            _tmpCholesterolMg = _cursor.getFloat(_cursorIndexOfCholesterolMg);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _item = new FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProteinGrams,_tmpCarbsGrams,_tmpFatGrams,_tmpFiberGrams,_tmpSugarGrams,_tmpSodiumMg,_tmpCholesterolMg,_tmpMealType,_tmpDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTotalCaloriesForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(calories) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalProteinForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(proteinGrams) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalCarbsForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(carbsGrams) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalFatForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(fatGrams) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalFiberForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(fiberGrams) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalSugarForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(sugarGrams) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalSodiumForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(sodiumMg) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalCholesterolForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(cholesterolMg) FROM food_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<FoodEntry> getEntryById(final long id) {
    final String _sql = "SELECT * FROM food_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<FoodEntry>() {
      @Override
      @Nullable
      public FoodEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProteinGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinGrams");
          final int _cursorIndexOfCarbsGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsGrams");
          final int _cursorIndexOfFatGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fatGrams");
          final int _cursorIndexOfFiberGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberGrams");
          final int _cursorIndexOfSugarGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarGrams");
          final int _cursorIndexOfSodiumMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumMg");
          final int _cursorIndexOfCholesterolMg = CursorUtil.getColumnIndexOrThrow(_cursor, "cholesterolMg");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final FoodEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpCalories;
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories);
            final float _tmpProteinGrams;
            _tmpProteinGrams = _cursor.getFloat(_cursorIndexOfProteinGrams);
            final float _tmpCarbsGrams;
            _tmpCarbsGrams = _cursor.getFloat(_cursorIndexOfCarbsGrams);
            final float _tmpFatGrams;
            _tmpFatGrams = _cursor.getFloat(_cursorIndexOfFatGrams);
            final float _tmpFiberGrams;
            _tmpFiberGrams = _cursor.getFloat(_cursorIndexOfFiberGrams);
            final float _tmpSugarGrams;
            _tmpSugarGrams = _cursor.getFloat(_cursorIndexOfSugarGrams);
            final float _tmpSodiumMg;
            _tmpSodiumMg = _cursor.getFloat(_cursorIndexOfSodiumMg);
            final float _tmpCholesterolMg;
            _tmpCholesterolMg = _cursor.getFloat(_cursorIndexOfCholesterolMg);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _result = new FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProteinGrams,_tmpCarbsGrams,_tmpFatGrams,_tmpFiberGrams,_tmpSugarGrams,_tmpSodiumMg,_tmpCholesterolMg,_tmpMealType,_tmpDateMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
