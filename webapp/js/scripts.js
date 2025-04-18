// $(".qna-comment").on("click", ".answerWrite input[type=submit]", addAnswer);
$(document).ready(function() {
  $(".answerWrite input[type=submit]").click(addAnswer);

  function addAnswer(e) {
    console.log("ajax 실행 성공: addAnswer");
    e.preventDefault();

    var queryString = $("form[name=answer]").serialize();
    console.log("data:", queryString);

    jQuery.ajax({
      type: 'post',
      url: '/api/qna/addAnswer',
      data: queryString,
      dataType: 'json',
      // contentType: 'application/json;charset=UTF-8',
      error: onError,
      success: onSuccess,
    });
  }

  function onSuccess(json, status) {
    console.log("addAnswer 성공!");
    console.log("data:", json);
    $("form[name=answer]")[0].reset();
    var answerData = json.answer[0];
    var answerTemplate = $("#answerTemplate").html();
    var template = answerTemplate.format(answerData.writer, new Date(answerData.createdDate), answerData.contents, answerData.answerId, answerData.answerId);
    $(".qna-comment-slipp-articles").prepend(template);
  }

  function onError(xhr, status) {
    alert("error");
  }

  $(".qna-comment").on("click", ".form-delete", deleteAnswer);

  function deleteAnswer(e) {
    console.log("ajax 실행 성공: deleteAnswer");
    e.preventDefault();

    var deleteBtn = $(this);
    var queryString = deleteBtn.closest("form").serialize();

    $.ajax({
      type: 'post',
      url: "/api/qna/deleteAnswer",
      data: queryString,
      // dataType: 'json',
      error: function (xhr, status) {
        alert("error: " + xhr.responseText);
      },
      success: function (json, status) {
        console.log("deleteAnswer 성공!")
        console.log("json:", json);
        if (json.result.status) {
          console.log("정상 응답 완료!");
          deleteBtn.closest('article').remove();
        }
      }
    });
  }

  String.prototype.format = function () {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function (match, number) {
      return typeof args[number] != 'undefined'
          ? args[number]
          : match
          ;
    });
  };
});