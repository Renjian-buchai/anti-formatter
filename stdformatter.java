/**
 * @file stdformatter.java
 * @author Erichtoaster
 * @brief The old one for future reference
 */

#include <fstream>
#include <iostream>
#include <vector>

#define TOKENTYPE_Separator 0
#define TOKENTYPE_Segment 1

/*does not support comments. except this one for some reason.*/
struct token {
  std::string contents;
  int type;
};

std::string trimSpaces(std::string input, int tokentype) {
  if (input.empty()) {
    return " ";
  }
  if (tokentype == TOKENTYPE_Segment) {
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
  int currentType = TOKENTYPE_Separator;
  std::string currentString = "";
  bool isStringLiteral = false;
  bool isCharLiteral = false;
  bool isEscapeSequence = false;
  bool tokenStartedWithSharp = false;
  bool forceNewToken = false;
  char character;
  bool endOfFile = false;
  while (!endOfFile) {
    if (!sourcefile.get(character)) {
      endOfFile = true;
      forceNewToken = true;
      character = 'F';
    }

    if (character == '"' && isCharLiteral == false && isEscapeSequence == false) {
      if (isStringLiteral == false) {
        isStringLiteral = true;
      } else {
        isStringLiteral = false;
      }
    }

    if (character == '\'' && isStringLiteral == false && isEscapeSequence == false) {
      if (isCharLiteral == false) {
        isCharLiteral = true;
      } else {
        isCharLiteral = false;
      }
    }

    if (character == '\\' && isEscapeSequence == false) {
      isEscapeSequence = true;
    } else {
      isEscapeSequence = false;
    }

    if (character == '\n' && tokenStartedWithSharp) {
      forceNewToken = true;
    } else if (character == '\n' || character == '\t' || character == ' ') {
      currentString.push_back(' ');
    } else if (isStringLiteral == false && isCharLiteral == false
        && (character == '(' || character == ')' || character == '[' || character == ']'
            || character == '{' || character == '}' || character == ';' || character == '|'
            || character == '&')) {
      if (currentType == TOKENTYPE_Separator && !endOfFile && !forceNewToken) {
        currentString.push_back(character);
      } else {
        if (forceNewToken) {
          forceNewToken = false;
        }
        token newToken;
        newToken.contents = currentString;
        newToken.type = currentType;
        allTokens.push_back(newToken);

        currentString = "";
        currentType = TOKENTYPE_Separator;

        currentString.push_back(character);
        tokenStartedWithSharp = false;
      }

    } else {
      if (currentType == TOKENTYPE_Segment && !endOfFile && !forceNewToken) {
        currentString.push_back(character);
      } else {
        if (forceNewToken) {
          forceNewToken = false;
        }
        token newToken;
        newToken.contents = currentString;
        newToken.type = currentType;
        allTokens.push_back(newToken);

        currentString = "";
        currentType = TOKENTYPE_Segment;

        currentString.push_back(character);
        if (character == '#') {
          tokenStartedWithSharp = true;
        } else {
          tokenStartedWithSharp = false;
        }
      }
    }
  }

  sourcefile.close();

  for (size_t i = 0; i < allTokens.size(); i++) {
    allTokens[i].contents = trimSpaces(allTokens[i].contents, allTokens[i].type);
  }

  unsigned int longestSeparatorChain = 0;
  unsigned int longestSegment = 0;
  for (size_t i = 0; i < allTokens.size(); i++) {
    if (allTokens[i].type == TOKENTYPE_Segment) {
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

  unsigned int whitespace = 0;
  int lastType = TOKENTYPE_Segment;
  for (size_t i = 0; i < allTokens.size(); i++) {
    switch (allTokens[i].type) {
      case TOKENTYPE_Separator:
        if (lastType == TOKENTYPE_Separator) {
          targetfile << "\n";
        }

        whitespace = longestSeparatorChain - allTokens[i].contents.length();

        targetfile << allTokens[i].contents;
        for (size_t j = 0; j < whitespace; j++) {
          targetfile << " ";
        }
        targetfile << " ";
        lastType = TOKENTYPE_Separator;
        break;
      case TOKENTYPE_Segment:
        if (lastType == TOKENTYPE_Segment) {
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
        lastType = TOKENTYPE_Segment;
        break;
    }
  }
  targetfile.close();
}