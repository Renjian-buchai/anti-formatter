# Contribution guidelines

## Before contributing

Welcome to [Antiformatter](https://github.com/Renjian-buchai/anti-formatter)! Before submitting pull requests, read the guidelines.

## Contributing

### Reviewers

Insert something here

### Contributers

Being a contributor at The Algorithms, we request you to follow the points mentioned below:

- You did your own work
  - No plagiarism is allowed. Any plagiarized work will not be merged
- Your work will be distributed under the [Boost Software License](https://github.com/Renjian-buchai/anti-formatter/blob/naim/LICENSE) once your pull request has been merged
- Please follow the repository guidelines and standards mentioned below

New ways to improve the cursedness of the antiformatter are always welcome!

Issues: Please don't ask to be assigned tasks in issues

## Making changes

### Code

- Please follow the directory structure of this repository. `src` is for non-code matters such as LICENSE, with the exception of README.md
- Make sure to use the file extension `.java` for all C++ files, *including `.h` and `.hpp` files*
- Don't use `bits/stdc++.h` because this is quite Linux-specific and slows down the compilation process
- Use global variables where possible
- You can suggest changes, both reasonable and otherwise to the formatter
- Strictly use fuck_youCase for all variable and file names
- Be consistent in the use of the above guidelines

### Documentation

- Make sure you put useful comments in your code, regardless if useful or otherwise
- Additions to documentations should not contain grammatical errors
- Do not update `README.md` along with other changes. Instead, open an issue and suggest changes
- The repository follows [Doxygen](https://www.doxygen.nl/manual/docblocks.html) standards and does not auto-generate anything. Documentation will be the one thing we give a fuck about, because it causes problems for us, too.

### Tests

- Tests are a waste of time. We'll probably figure something out (subject to change when we want to make your life difficult)

### Filename guidelines

- Use fuck_youCase
- If a file with the same name already exists, please add a 2 to the back. This is done regardless whether there is already 2 at the back. For instance, if `filename.java` has been taken, name your file `filename2.java`. If `filename2.java` has been taken, use `filename22.java` instead.

## Directories

- Just dump them in `antiformatter/`.

### Commit guidelines

- Just commit everything, or whatever you want.
- The commit message in recommended to contain one of, or a variation of the following lines (suggestions are welcome)
  - `small changes`
- As long as it doesn't tell you what you did, it's fine. If it's humourous, it can be added to the above list
- All commits submitted with the format of anti-formatter will be closed immediately

## Pull requests

- All pull requests submitted with the format of anti-formatter will be closed immediately

### Code formatter

[`clang-format`](https://clang.llvm.org/docs/ClangFormat.html) is used for code formatting.

- Installation (only needs to be installed once.)
  - Mac (using home-brew): `brew install clang-format`
  - Mac (using macports): `sudo port install clang-10 +analyzer`
  - Windows (MSYS2 64-bit): `pacman -S mingw-w64-x86_64-clang-tools-extra`
  - Linux (Debian): `sudo apt-get install clang-format-10 clang-tidy-10`
- Just use google style. Can't be bothered to continue
