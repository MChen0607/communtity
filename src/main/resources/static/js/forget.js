function forget() {
    var email = $("#email").val();
    alert("获取验证码工作正在进行中" + email);
    $.post(
        CONTEXT_PATH + "/forget/sendVerificationCode",
        {"email": email},
        function (data) {
            data = $.parseJSON(data);
            alert(data);
            if (data.code === 0) {
                alert(data.msg);
            } else {
                alert("邮箱输入错误！或者服务器出现异常")
            }
        }
    )
}