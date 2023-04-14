
const loginTemplate = `<div class="login-container">
<div id="login-form">
    <i class="fa-solid fa-xmark close-btn"></i>
    <h1>Login Member</h1>
    <form>
        <div class="mb-3">
            <label for="exampleInputEmail1" class="form-label">Email address</label>
            <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp">
            <div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>
        </div>
        <div class="mb-3">
            <label for="exampleInputPassword1" class="form-label">Password</label>
            <input type="password" class="form-control" id="exampleInputPassword1">
        </div>
        <div class="mb-3 form-check">
            <input type="checkbox" class="form-check-input" id="exampleCheck1">
            <label class="form-check-label" for="exampleCheck1">Check me out</label>
        </div>
        <button type="submit" class="signin-btn">Submit</button>
        <div class="login-footer">
            <a href="" class="forget">Forgot your password?</a>
            <a href="" class="sign-up">Don't have your account?</a>
        </div>
    </form>
</div>
</div>`;
const body = document.body;
body.insertAdjacentHTML("afterbegin", loginTemplate);

const loginContainer = document.querySelector(".login-container");
const closeBtn = document.querySelector(".close-btn");
const signInBtn = document.querySelector(".sign-in");

signInBtn.addEventListener("click", showLoginForm);

function showLoginForm() {
    if (loginContainer) {
        setTimeout(function() {
            loginContainer.classList.add("is-show");
        }, 500);
    }
}

closeBtn.addEventListener("click", closeLoginForm);

function closeLoginForm() {
    if (loginContainer) {
        loginContainer.classList.remove("is-show");
    }
}

document.addEventListener("click", clickOutsideLoginForm);

function clickOutsideLoginForm(event) {
    console.log(event.target);
    if (event.target.matches(".login-container")) {
        loginContainer.classList.remove("is-show");
    }
}