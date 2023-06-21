#include <fstream>
#include <iostream>
#include <cstdint>
#include <vector>
#include <cassert>
#include "header.java"

enum tokens : uint8_t { token_sep, token_seg }
;

/**
 * @brief Formatter flags
 *
 * @note Bits:
 * @note 0: is_stringLiteral
 * @note 1: is_charLiteral
 * @note 2: token_startedWithsharp
 * @note 3: force_newToken
 * @note 4: end_ofFile
 * @note 5: is_escapeSquence
 */
uint8_t flags = 0;

int main(int argc, char* argv[]) {
  int8_t return_value = 0;
  (void) (argc != 2 ? std::cerr << "Usage: " << argv[0] << " <sourcefile>\n", ++return_value : 0);
  std::vector<token> allTokens = {};
  { // Scope operator to clear ram, since else, ifstream etc. will stay till the end of main.
    std::ifstream sourcefile(argv[1], std::ios::in);
    (void) (!sourcefile ? std::cerr << "Faled to open file " << argv[1] << "\n",
        ++return_value  : 0);

    uint8_t currentType = token_sep;
    std::string currentString = "";

    if (return_value)
      return return_value;

    char character;
    while (!(flags & (1 << 4))) {
      (void) (!sourcefile.get(character) ? flags |= (1 << 4), flags |= (1 << 3),
          (void) (character = 'F')       : (void) 0);

      is_stringLiteral(character, flags);
      is_charLiteral(character, flags);
      is_escapeSequence(character, flags);

      if (character == '\n' && flags & (1 << 2)) {
        flags |= (1 << 3);
      } else if (character == '\n' || character == '\t' || character == ' ') {
        currentString.push_back(' ');
      } else if (!(flags & 1) && !(flags & (1 << 1))
          && (character == '(' || character == ')' || character == '[' || character == ']'
              || character == '{' || character == '}' || character == ';' || character == '|'
              || character == '&')) {
        if (currentType == token_sep && !(flags & (1 << 4)) && !(flags & (1 << 3))) {
          currentString.push_back(character);
        } else {
          /* To be extracted */ {
            token newToken;
            newToken.contents = currentString;
            newToken.type = currentType;
            allTokens.push_back(newToken);

            currentString = "";
            currentType = token_sep;

            currentString.push_back(character);
            flags &= ~(1 << 2);
          }
        }
      } else {
        if (currentType == token_seg && !(flags & (1 << 4)) && !(flags & (1 << 3))) {
          currentString.push_back(character);
        } else {
          /* To be extracted */ {
            if (flags & (1 << 3)) {
              flags &= ~(1 << 3);
            }
            token newToken;
            newToken.contents = currentString;
            newToken.type = currentType;
            allTokens.push_back(newToken);

            currentString = "";
            currentType = token_seg;

            currentString.push_back(character);
            if (character == '#') {
              flags |= (1 << 2);
            } else {
              flags &= ~(1 << 2);
            }
          }
        }
      }
    }

    sourcefile.close();
    assert (!sourcefile.is_open());
  }

  for (size_t i = 0; i < allTokens.size(); i++) {
    allTokens[i].contents = trim_spaces(allTokens[i].contents, allTokens[i].type);
  }

  uint32_t longestSeparatorChain = 0;
  uint32_t longestSegment = 0;
  for (size_t i = 0; i < allTokens.size(); i++)
    (void) (allTokens[i].type == token_seg
            ? longestSegment = allTokens[i].contents.length() > longestSegment
                ? allTokens[i].contents.length()
                : longestSegment
            : longestSeparatorChain = allTokens[i].contents.length() > longestSeparatorChain
                ? allTokens[i].contents.length()
                : longestSeparatorChain);

  std::string outputfilename = std::string(argv[1]) + ".out";
  std::ofstream targetfile(outputfilename.c_str(), std::ios::out);
  (void) (!targetfile ? std::cerr << "Failed to create file " << argv[1] << ".out"
                                  << "\n",
      ++return_value : 0);

  if (return_value)
    return return_value;

  uint32_t whitespace = 0;
  uint8_t lastType = token_seg;
  file_creator(allTokens, lastType, longestSeparatorChain, targetfile, whitespace, longestSegment);
  targetfile.close();

  assert (!targetfile.is_open());

  std::cout << 1;
}

std::string trim_spaces(const std::string& input, const uint8_t& token_type, std::string to_return,
    size_t first_nonSpace) {
  if (input.empty())
    goto tor;
  if (token_type == token_seg) {
    first_nonSpace = input.find_first_not_of(' ');
    to_return = first_nonSpace == std::string::npos
        ? ""
        : input.substr(first_nonSpace, input.find_last_not_of(' ') - first_nonSpace + 1);
    goto tor;
  }
  to_return = "";
  for (char c : input) (void) (c == ' ' ? (void) 0 : (void) (to_return += c));
tor:
  return to_return;
}

void is_stringLiteral(const char& character, uint8_t& flags) {
  if (character == '"' && !(flags & (1 << 1)) && !(flags & (1 << 5))) {
    if (!(flags & 1)) {
      flags |= 1;
    } else {
      flags &= ~1;
    }
  }
}

void is_charLiteral(const char& character, uint8_t& flags) {
  if (character == '\'' && !(flags & 1) && !(flags & (1 << 5))) {
    if (!(flags & (1 << 1))) {
      flags |= 1 << 1;
    } else {
      flags &= ~(1 << 1);
    }
  }
}

void is_escapeSequence(const char& character, uint8_t& flags) {
  if (character == '\\' && !(flags & (1 << 5))) {
    flags |= (1 << 5);
  } else {
    flags &= ~(1 << 5);
  }
}

void file_creator(std::vector<token>& allTokens, uint8_t& lastType, uint32_t& longestSeparatorChain,
    std::ofstream& targetfile, uint32_t& whitespace, uint32_t& longestSegment) {
  for (size_t i = 0; i < allTokens.size(); i++) {
    if (allTokens[i].type) {
      if (lastType == token_seg) {
        for (size_t j = 0; j < longestSeparatorChain; j++) targetfile << " ";
        targetfile << " ";
      }

      whitespace = longestSegment - allTokens[i].contents.length();

      for (size_t j = 0; j < whitespace / 2; j++) targetfile << " ";

      targetfile << allTokens[i].contents << "\n";
      lastType = token_seg;
      goto here;
    }
    (void) (lastType == token_sep ? (void) (targetfile << "\n") : (void) 0);

    whitespace = longestSeparatorChain - allTokens[i].contents.length();
    targetfile << allTokens[i].contents;

    for (size_t j = 0; j < whitespace; j++) targetfile << " ";
    targetfile << " ";

    lastType = token_sep;
  here:
    (void) 0; // noop to silence warning. Will be optimised out if >O1
  }
}
