// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static List<String> comments; 

  @Override
  public void init() {
    comments = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int length = Integer.parseInt(request.getParameter("length"));
    String language = request.getParameter("language");

    clearComments();

    PreparedQuery results = retrieveQueryResults("Task", "timestamp");

    addComments(length, language, results);

    response.setContentType("application/json;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(convertToJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");
    long timestamp = System.currentTimeMillis();

    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("comment", comment);
    taskEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.sendRedirect("/index.html");
  }

  private String convertToJson(List<String> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  private void clearComments() {
    comments.clear();
  }

  private PreparedQuery retrieveQueryResults(String key, String value) {
    Query query = new Query(key).addSort(value, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    return datastore.prepare(query);
  }

  private void addComments(int length, String language, PreparedQuery results) {
    Translate translate = TranslateOptions.getDefaultInstance().getService();

    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(length))) {
      String comment = (String) entity.getProperty("comment");
      Translation translation = 
      translate.translate(comment, Translate.TranslateOption.targetLanguage(language));

      comments.add(translation.getTranslatedText());
    }
  }
}