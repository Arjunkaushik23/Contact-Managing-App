console.log("Script loaded");

let currentTheme = getTheme();

document.addEventListener("DOMContentLoaded",() => {
    changeTheme();
});

function changeTheme() {
    changePageTheme(currentTheme);

    const changeThemeButton = document.querySelector('#theme_change_button');
    changeThemeButton.addEventListener('click', () => {
        console.log('theme_change_button clicked');
        currentTheme = currentTheme === 'dark' ? 'light' : 'dark';
        changePageTheme(currentTheme);
    });
}

function setTheme(theme) {
    localStorage.setItem('theme', theme);
}

function getTheme() {
    let theme = localStorage.getItem('theme');
    return theme ? theme : 'light';
}

function changePageTheme(theme) {
    setTheme(theme);
    document.querySelector('html').classList.remove('light', 'dark');
    document.querySelector('html').classList.add(theme);
    document.querySelector('#theme_change_button').querySelector('span').textContent = theme === 'light' ? 'Dark' : 'Light';
}
