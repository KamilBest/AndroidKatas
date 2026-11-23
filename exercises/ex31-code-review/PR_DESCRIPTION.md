# PR #482: Patient profile screen

Adds a profile screen with patient data (name, PESEL, policy) and a "Log out" button.
- the profile is loaded from the API when the screen opens,
- the token is saved after login, so the user doesn't have to log in again,
- a counter of days until the policy ends,
- I added logs to make debugging in production easier.

Tested by hand on a Pixel 8. No unit tests, because the ViewModel is hard to test.
