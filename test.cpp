#include <cstdint>
#include <fstream>
#include <iostream>
#include <vector>

enum tokens : uint8_t { token_sep, token_seg };

/*does not support comments. except this one for some reason.*/
struct token {
  std::string contents;
  uint8_t type;
};

uint8_t flags = 0;

bool isStringLiteral = false;
bool isCharLiteral = false;
bool isEscapeSequence = false;
bool tokenStartedWithSharp = false;
bool forceNewToken = false;
bool endOfFile = false;

std::string trimSpaces(std::string, uint8_t);

int main(int argc, char* argv[]) {
  if (argc != 2) {
    std::cerr << "Usage: " << argv[0] << " <sourcefile>\n";
    return 0;
  }
  std::ifstream sourcefile(argv[1], std::ios::in);
  if (!sourcefile) {
    std::cerr << "Failed to open file " << argv[1] << "\n";
    return 0;
  }

  std::vector<token> allTokens = {};
  uint8_t currentType = token_sep;
  std::string currentString = "";

  char character;
  while (!(flags & (1 << 5))) {
    if (!sourcefile.get(character)) {
      flags |= (1 << 5);
      flags |= (1 << 4);
      character = 'F';
    }

    if (character == '"' && !(flags & (1 << 1)) && !(flags & (1 << 2))) {
      if (flags & (1 << 0)) {
        flags |= (1 << 0);
      } else {
        flags ^= (1 << 0);
      }
    }

    if (character == '\'' && !(flags & (1 << 0)) && !(flags & (1 << 2))) {
      if (!(flags & (1 << 1))) {
        flags |= (1 << 1);
      } else {
        flags ^= (1 << 1);
      }
    }

    if (character == '\\' && !(flags & (1 << 2))) {
      flags |= (1 << 2);
    } else {
      flags ^= (1 << 2);
    }

    if (character == '\n' && flags & (1 << 3)) {
      flags |= (1 << 4);
    } else if (character == '\n' || character == '\t' || character == ' ') {
      currentString.push_back(' ');
    } else if (!(flags & ((1 << 0))) && !(flags & (1 << 1)) &&
               (character == '(' || character == ')' || character == '[' ||
                character == ']' || character == '{' || character == '}' ||
                character == ';' || character == '|' || character == '&')) {
      if (currentType == token_sep && !(flags & (1 << 5)) &&
          !(flags & (1 << 4))) {
        currentString.push_back(character);
      } else {
        if (flags & (1 << 4)) {
          flags ^= (1 << 4);
        }
        token newToken;
        newToken.contents = currentString;
        newToken.type = currentType;
        allTokens.push_back(newToken);

        currentString = "";
        currentType = token_sep;

        currentString.push_back(character);
        flags ^= (1 << 3);
      }

    } else {
      if (currentType == token_seg && !(flags & (1 << 5)) &&
          !(flags & (1 << 4))) {
        currentString.push_back(character);
      } else {
        if (flags & (1 << 4)) {
          flags ^= (1 << 4);
        }
        token newToken;
        newToken.contents = currentString;
        newToken.type = currentType;
        allTokens.push_back(newToken);

        currentString = "";
        currentType = token_seg;

        currentString.push_back(character);
        if (character == '#') {
          flags |= (1 << 3);
        } else {
          flags ^= (1 << 3);
        }
      }
    }
  }

  sourcefile.close();

  for (size_t i = 0; i < allTokens.size(); i++) {
    allTokens[i].contents =
        trimSpaces(allTokens[i].contents, allTokens[i].type);
  }

  uint32_t longestSeparatorChain = 0;
  uint32_t longestSegment = 0;
  for (size_t i = 0; i < allTokens.size(); i++) {
    if (allTokens[i].type == token_seg) {
      if (allTokens[i].contents.length() > longestSegment) {
        longestSegment = allTokens[i].contents.length();
      }
    } else {
      if (allTokens[i].contents.length() > longestSeparatorChain) {
        longestSeparatorChain = allTokens[i].contents.length();
      }
    }
  }

  std::string outputfilename = std::string(argv[1]) + ".out";
  std::ofstream targetfile(outputfilename.c_str(), std::ios::out);
  if (!targetfile) {
    std::cerr << "Failed to create file " << argv[1] << ".out"
              << "\n";
    return 0;
  }

  uint32_t whitespace = 0;
  uint8_t lastType = token_seg;
  for (size_t i = 0; i < allTokens.size(); i++) {
    switch (allTokens[i].type) {
      case token_sep:
        if (lastType == token_sep) {
          targetfile << "\n";
        }

        whitespace = longestSeparatorChain - allTokens[i].contents.length();

        targetfile << allTokens[i].contents;
        for (size_t j = 0; j < whitespace; j++) {
          targetfile << " ";
        }
        targetfile << " ";
        lastType = token_sep;
        break;
      case token_seg:
        if (lastType == token_seg) {
          for (size_t j = 0; j < longestSeparatorChain; j++) {
            targetfile << " ";
          }
          targetfile << " ";
        }

        whitespace = longestSegment - allTokens[i].contents.length();

        for (size_t j = 0; j < whitespace / 2; j++) {
          targetfile << " ";
        }
        targetfile << allTokens[i].contents << "\n";
        lastType = token_seg;
        break;
    }
  }
  targetfile.close();
}

std::string trimSpaces(std::string input, uint8_t tokentype) {
  if (input.empty()) {
    return " ";
  }
  if (tokentype == token_seg) {
    std::size_t firstNonSpace = input.find_first_not_of(' ');
    if (firstNonSpace == std::string::npos) {
      return "";
    }
    std::size_t lastNonSpace = input.find_last_not_of(' ');
    return input.substr(firstNonSpace, lastNonSpace - firstNonSpace + 1);
  } else {
    std::string toReturn = "";
    for (char c : input) {
      if (c != ' ') {
        toReturn += c;
      }
    }
    return toReturn;
  }
}
