Oliver Tonnesen

Help Received:
- https://brennan.io/2015/01/16/write-a-shell-in-c/

This website was extremely useful in helping me when I first started work on this assignment,
and the basic flow of operation (read, parse, execute) was heavily inspired by this resource.

Additional Features:
- `history` lists prior commands beginning with most recent
- ability to repeat commands using `!prefix`
- `getenv env` retrieves value of the `env` environment variable
- prompt displays working directory (can be enabled by appending `setenv FANCYPROMPT 1`
to your, `.kapishrc`, or by otherwise setting the `FANCYPROMPT` environment variable to
any value)

Known Bugs:
- Ctrl+D closes all instances of kapish opened from within kapish (Only on my machine, works
as intended on UVic's linux machines :D)
