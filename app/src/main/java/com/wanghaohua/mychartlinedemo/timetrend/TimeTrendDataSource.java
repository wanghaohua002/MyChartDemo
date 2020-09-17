package com.wanghaohua.mychartlinedemo.timetrend;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghaohua on 2020/9/16
 */
public class TimeTrendDataSource {
    public List<TimeTrendModel> getData(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open("minute.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        TimeTrendResult timeTrendResult = gson.fromJson(stringBuilder.toString(), TimeTrendResult.class);
        if (timeTrendResult != null) {
            return timeTrendResult.data;
        }
        return new ArrayList<>();
    }

    public float getPrePrice() {
        return 5.40f;
    }

    public TimeTrendTag getTimeTag() {
        return new TimeTrendTag("9:00", "11:30/13:00", "15:00");
    }
}
