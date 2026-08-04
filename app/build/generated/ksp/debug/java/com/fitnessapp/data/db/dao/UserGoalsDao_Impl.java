package com.fitnessapp.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fitnessapp.data.db.entity.UserGoals;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserGoalsDao_Impl implements UserGoalsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserGoals> __insertionAdapterOfUserGoals;

  public UserGoalsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserGoals = new EntityInsertionAdapter<UserGoals>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_goals` (`id`,`dailyCalorieGoal`,`dailyProteinGoal`,`dailyCarbsGoal`,`dailyFatGoal`,`dailyFiberGoal`,`dailySugarGoal`,`dailySodiumGoal`,`dailyCholesterolGoal`,`dailyWaterGoal`,`dailySleepGoalHours`,`dailyStepsGoal`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserGoals entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDailyCalorieGoal());
        statement.bindDouble(3, entity.getDailyProteinGoal());
        statement.bindDouble(4, entity.getDailyCarbsGoal());
        statement.bindDouble(5, entity.getDailyFatGoal());
        statement.bindDouble(6, entity.getDailyFiberGoal());
        statement.bindDouble(7, entity.getDailySugarGoal());
        statement.bindDouble(8, entity.getDailySodiumGoal());
        statement.bindDouble(9, entity.getDailyCholesterolGoal());
        statement.bindLong(10, entity.getDailyWaterGoal());
        statement.bindDouble(11, entity.getDailySleepGoalHours());
        statement.bindLong(12, entity.getDailyStepsGoal());
      }
    };
  }

  @Override
  public long insert(final UserGoals userGoals) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfUserGoals.insertAndReturnId(userGoals);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Flow<UserGoals> getUserGoals() {
    final String _sql = "SELECT * FROM user_goals WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_goals"}, new Callable<UserGoals>() {
      @Override
      @Nullable
      public UserGoals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyCalorieGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCalorieGoal");
          final int _cursorIndexOfDailyProteinGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyProteinGoal");
          final int _cursorIndexOfDailyCarbsGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCarbsGoal");
          final int _cursorIndexOfDailyFatGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyFatGoal");
          final int _cursorIndexOfDailyFiberGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyFiberGoal");
          final int _cursorIndexOfDailySugarGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailySugarGoal");
          final int _cursorIndexOfDailySodiumGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailySodiumGoal");
          final int _cursorIndexOfDailyCholesterolGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCholesterolGoal");
          final int _cursorIndexOfDailyWaterGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyWaterGoal");
          final int _cursorIndexOfDailySleepGoalHours = CursorUtil.getColumnIndexOrThrow(_cursor, "dailySleepGoalHours");
          final int _cursorIndexOfDailyStepsGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyStepsGoal");
          final UserGoals _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpDailyCalorieGoal;
            _tmpDailyCalorieGoal = _cursor.getInt(_cursorIndexOfDailyCalorieGoal);
            final float _tmpDailyProteinGoal;
            _tmpDailyProteinGoal = _cursor.getFloat(_cursorIndexOfDailyProteinGoal);
            final float _tmpDailyCarbsGoal;
            _tmpDailyCarbsGoal = _cursor.getFloat(_cursorIndexOfDailyCarbsGoal);
            final float _tmpDailyFatGoal;
            _tmpDailyFatGoal = _cursor.getFloat(_cursorIndexOfDailyFatGoal);
            final float _tmpDailyFiberGoal;
            _tmpDailyFiberGoal = _cursor.getFloat(_cursorIndexOfDailyFiberGoal);
            final float _tmpDailySugarGoal;
            _tmpDailySugarGoal = _cursor.getFloat(_cursorIndexOfDailySugarGoal);
            final float _tmpDailySodiumGoal;
            _tmpDailySodiumGoal = _cursor.getFloat(_cursorIndexOfDailySodiumGoal);
            final float _tmpDailyCholesterolGoal;
            _tmpDailyCholesterolGoal = _cursor.getFloat(_cursorIndexOfDailyCholesterolGoal);
            final int _tmpDailyWaterGoal;
            _tmpDailyWaterGoal = _cursor.getInt(_cursorIndexOfDailyWaterGoal);
            final float _tmpDailySleepGoalHours;
            _tmpDailySleepGoalHours = _cursor.getFloat(_cursorIndexOfDailySleepGoalHours);
            final int _tmpDailyStepsGoal;
            _tmpDailyStepsGoal = _cursor.getInt(_cursorIndexOfDailyStepsGoal);
            _result = new UserGoals(_tmpId,_tmpDailyCalorieGoal,_tmpDailyProteinGoal,_tmpDailyCarbsGoal,_tmpDailyFatGoal,_tmpDailyFiberGoal,_tmpDailySugarGoal,_tmpDailySodiumGoal,_tmpDailyCholesterolGoal,_tmpDailyWaterGoal,_tmpDailySleepGoalHours,_tmpDailyStepsGoal);
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
