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

async function loadComments() {
  fetch('/data?length=10')
    .then(response => response.json())
    .then((comments) => {
      const commentSectionContainer = document.getElementById('commentSection');
      console.log(comments);
      comments.forEach((comment) => {
        commentSectionContainer.innerHTML += '<div class="card"> <div class="card-body text-primary">' + comment + ' </div> </div>';
      })
    });
}

async function refreshComments(commentLength) {
  fetch('/data?length=' + commentLength.value)
    .then(response => response.json())
    .then((comments) => {
      const commentSectionContainer = document.getElementById('commentSection');
      console.log(comments);
      commentSectionContainer.innerHTML = '';
      comments.forEach((comment) => {
        commentSectionContainer.innerHTML += '<div class="card"> <div class="card-body text-primary">' + comment + ' </div> </div>';
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