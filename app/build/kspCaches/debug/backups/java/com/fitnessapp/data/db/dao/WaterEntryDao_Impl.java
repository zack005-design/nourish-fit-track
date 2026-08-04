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
import com.fitnessapp.data.db.entity.WaterEntry;
import java.lang.Class;
import java.lang.Exception;
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
public final class WaterEntryDao_Impl implements WaterEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WaterEntry> __insertionAdapterOfWaterEntry;

  private final EntityDeletionOrUpdateAdapter<WaterEntry> __deletionAdapterOfWaterEntry;

  private final EntityDeletionOrUpdateAdapter<WaterEntry> __updateAdapterOfWaterEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForDateRange;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public WaterEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWaterEntry = new EntityInsertionAdapter<WaterEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `water_entries` (`id`,`dateMillis`,`amountMl`,`timestampMillis`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WaterEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindLong(3, entity.getAmountMl());
        statement.bindLong(4, entity.getTimestampMillis());
      }
    };
    this.__deletionAdapterOfWaterEntry = new EntityDeletionOrUpdateAdapter<WaterEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `water_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WaterEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWaterEntry = new EntityDeletionOrUpdateAdapter<WaterEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `water_entries` SET `id` = ?,`dateMillis` = ?,`amountMl` = ?,`timestampMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WaterEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindLong(3, entity.getAmountMl());
        statement.bindLong(4, entity.getTimestampMillis());
        statement.bindLong(5, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteForDateRange = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM water_entries WHERE dateMillis >= ? AND dateMillis <= ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM water_entries";
        return _query;
      }
    };
  }

  @Override
  public long insert(final WaterEntry entry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfWaterEntry.insertAndReturnId(entry);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final WaterEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __deletionAdapterOfWaterEntry.handle(entry);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final WaterEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __updateAdapterOfWaterEntry.handle(entry);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteForDateRange(final long startOfDay, final long endOfDay) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForDateRange.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, endOfDay);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteForDateRange.release(_stmt);
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
  public Flow<List<WaterEntry>> getEntriesForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT * FROM water_entries WHERE dateMillis >= ? AND dateMillis <= ? ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"water_entries"}, new Callable<List<WaterEntry>>() {
      @Override
      @NonNull
      public List<WaterEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfAmountMl = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMl");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final List<WaterEntry> _result = new ArrayList<WaterEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WaterEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final int _tmpAmountMl;
            _tmpAmountMl = _cursor.getInt(_cursorIndexOfAmountMl);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            _item = new WaterEntry(_tmpId,_tmpDateMillis,_tmpAmountMl,_tmpTimestampMillis);
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
  public Flow<Integer> getTotalWaterForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT SUM(amountMl) FROM water_entries WHERE dateMillis >= ? AND dateMillis <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"water_entries"}, new Callable<Integer>() {
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
  public Flow<List<WaterEntry>> getAllWaterEntries() {
    final String _sql = "SELECT * FROM water_entries ORDER BY dateMillis DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"water_entries"}, new Callable<List<WaterEntry>>() {
      @Override
      @NonNull
      public List<WaterEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfAmountMl = CursorUtil.getColumnIndexOrThrow(_cursor, "amountMl");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final List<WaterEntry> _result = new ArrayList<WaterEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WaterEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final int _tmpAmountMl;
            _tmpAmountMl = _cursor.getInt(_cursorIndexOfAmountMl);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            _item = new WaterEntry(_tmpId,_tmpDateMillis,_tmpAmountMl,_tmpTimestampMillis);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
