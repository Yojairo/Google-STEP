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

google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  var data = google.visualization.arrayToDataTable([
    ["Element", "Density", { role: "style" }],
    ["Copper", 8.94, "#b87333"],
    ["Silver", 10.49, "silver"],
    ["Gold", 19.30, "gold"],
    ["Platinum", 21.45, "color: #e5e4e2"]
  ]);

  var view = new google.visualization.DataView(data);
  view.setColumns([0, 1,
    {
      calc: "stringify",
      sourceColumn: 1,
      type: "string",
      role: "annotation"
    },
    2]);

  var options = {
    title: "Density of Precious Metals, in g/cm^3",
    width: 600,
    height: 400,
    bar: { groupWidth: "95%" },
    legend: { position: "none" },
    backgroundColor: 'transparent'
  };

  var chart = new google.visualization.BarChart(document.getElementById("chart"));
  chart.draw(view, options);
}

async function refreshComments(commentLength, language) {
  fetch('/data?length=' + commentLength.value + '&language=' + language.value)
    .then(response => response.json())
    .then((comments) => {
      const commentSectionContainer = document.getElementById('commentSection');
      console.log(comments);
      commentSectionContainer.innerHTML = '';
      comments.forEach((comment) => {
        commentSectionContainer.innerHTML += '<div class="card"> <div lang="' +
          language.value + '" class="card-body text-primary">' + comment + ' </div> </div>';
      })
    });
}

async function deleteComments() {
  fetch('/delete-data', {
    method: 'post'
  })
    .then(() => {
      const commentSectionContainer = document.getElementById('commentSection');
      commentSectionContainer.innerHTML = '';
    });
}