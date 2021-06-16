package com.java.kaboome.data.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class StringTSMapConverter {
    @TypeConverter
    public static Map<String, Integer> getStringTSMapFromJson(String stringTSJson){
        Type mapType = new TypeToken<Map<String, Integer>>() {
        }.getType();
        return new Gson().fromJson(stringTSJson, mapType);
    }

    @TypeConverter
    public static String getStringTSJsonFromMap(Map<String, Integer> stringTSMap){
        Gson gson = new Gson();
        return gson.toJson(stringTSMap);
    }
}
