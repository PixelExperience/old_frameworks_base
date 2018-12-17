/*
 * Copyright (C) 2018 PixelExperience
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>.
 */

package com.android.systemui.ambient.play;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class AuddApi {
    static String sendRequest(AmbientIndicationManager manager, byte[] buffer) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.audd.io/").openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            StringBuilder params = new StringBuilder();
            params.append("api_token=8ec90ef80fd1b750c990642d6e17ccb9").append("&");
            params.append("local_lan=pt-BR").append("&");
            params.append("device_id=0.9513109616541061545060969071").append("&");
            params.append("version=2.0.0").append("&");
            params.append("app_id=ngpampappnmepgilojfohadhhmbhlaek").append("&");
            params.append("audio_format=wav").append("&");
            params.append("audio=" + Base64.encodeToString(buffer, Base64.DEFAULT));
            writer.write(params.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                StringBuilder response = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            if (manager.DEBUG) e.printStackTrace();
        }
        return null;
    }
}