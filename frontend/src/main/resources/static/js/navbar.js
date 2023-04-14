$(document).ready(function () {

    const adminMenuTemplate =
        `<ul class="admin-dropdown-menu">
            <li><a class="admin-dropdown-item" href="/user/info">Thông tin tài khoản</a></li>
            <li><a class="admin-dropdown-item" href="/admin/dashboard">Admin dashboard</a></li>
            <li><a class="admin-dropdown-item" href="/logout">Đăng xuất</a></li>
        </ul>`
    
   

    $(".admin-menu").append(adminMenuTemplate);

    $(".admin-menu").on("click", function() {
        $(".admin-dropdown-menu").toggleClass("show-admin-menu");
    })

/*    $(".admin-menu").on("click", closeFromOutside)

    function closeFromOutside(e) {
        console.log(e.target);
        if (!(e.target).has(".admin-dropdown-menu")) {
            $(".admin-dropdown-menu").remove("show-admin-menu");
        }
    }*/
})