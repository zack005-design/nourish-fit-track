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
import com.fitnessapp.data.db.entity.SleepEntry;
import java.lang.Class;
import java.lang.Exception;
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
public final class SleepEntryDao_Impl implements SleepEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SleepEntry> __insertionAdapterOfSleepEntry;

  private final EntityDeletionOrUpdateAdapter<SleepEntry> __deletionAdapterOfSleepEntry;

  private final EntityDeletionOrUpdateAdapter<SleepEntry> __updateAdapterOfSleepEntry;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public SleepEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSleepEntry = new EntityInsertionAdapter<SleepEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sleep_entries` (`id`,`startMillis`,`endMillis`,`quality`,`notes`,`dateMillis`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartMillis());
        statement.bindLong(3, entity.getEndMillis());
        statement.bindLong(4, entity.getQuality());
        statement.bindString(5, entity.getNotes());
        statement.bindLong(6, entity.getDateMillis());
      }
    };
    this.__deletionAdapterOfSleepEntry = new EntityDeletionOrUpdateAdapter<SleepEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sleep_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSleepEntry = new EntityDeletionOrUpdateAdapter<SleepEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sleep_entries` SET `id` = ?,`startMillis` = ?,`endMillis` = ?,`quality` = ?,`notes` = ?,`dateMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartMillis());
        statement.bindLong(3, entity.getEndMillis());
        statement.bindLong(4, entity.getQuality());
        statement.bindString(5, entity.getNotes());
        statement.bindLong(6, entity.getDateMillis());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sleep_entries";
        return _query;
      }
    };
  }

  @Override
  public long insert(final SleepEntry entry) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSleepEntry.insertAndReturnId(entry);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final SleepEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __deletionAdapterOfSleepEntry.handle(entry);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final SleepEntry entry) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __updateAdapterOfSleepEntry.handle(entry);
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
  public Flow<List<SleepEntry>> getEntriesForDateRange(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT * FROM sleep_entries WHERE dateMillis >= ? AND dateMillis <= ? ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_entries"}, new Callable<List<SleepEntry>>() {
      @Override
      @NonNull
      public List<SleepEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startMillis");
          final int _cursorIndexOfEndMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endMillis");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final List<SleepEntry> _result = new ArrayList<SleepEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartMillis;
            _tmpStartMillis = _cursor.getLong(_cursorIndexOfStartMillis);
            final long _tmpEndMillis;
            _tmpEndMillis = _cursor.getLong(_cursorIndexOfEndMillis);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _item = new SleepEntry(_tmpId,_tmpStartMillis,_tmpEndMillis,_tmpQuality,_tmpNotes,_tmpDateMillis);
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
  public Flow<List<SleepEntry>> getAllEntries() {
    final String _sql = "SELECT * FROM sleep_entries ORDER BY dateMillis DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_entries"}, new Callable<List<SleepEntry>>() {
      @Override
      @NonNull
      public List<SleepEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startMillis");
          final int _cursorIndexOfEndMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endMillis");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final List<SleepEntry> _result = new ArrayList<SleepEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartMillis;
            _tmpStartMillis = _cursor.getLong(_cursorIndexOfStartMillis);
            final long _tmpEndMillis;
            _tmpEndMillis = _cursor.getLong(_cursorIndexOfEndMillis);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _item = new SleepEntry(_tmpId,_tmpStartMillis,_tmpEndMillis,_tmpQuality,_tmpNotes,_tmpDateMillis);
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
  public Flow<SleepEntry> getEntryById(final long id) {
    final String _sql = "SELECT * FROM sleep_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_entries"}, new Callable<SleepEntry>() {
      @Override
      @Nullable
      public SleepEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startMillis");
          final int _cursorIndexOfEndMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endMillis");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final SleepEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartMillis;
            _tmpStartMillis = _cursor.getLong(_cursorIndexOfStartMillis);
            final long _tmpEndMillis;
            _tmpEndMillis = _cursor.getLong(_cursorIndexOfEndMillis);
            final int _tmpQuality;
            _tmpQuality = _cursor.getInt(_cursorIndexOfQuality);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            _result = new SleepEntry(_tmpId,_tmpStartMillis,_tmpEndMillis,_tmpQuality,_tmpNotes,_tmpDateMillis);
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
