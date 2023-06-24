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
  int8_t return_value = 0; // zero-initialises return-value, should stay till end of main.
  (void) (argc != 2  ? std::cerr << "Usage: " << argv[0] << " <sourcefile>\n",
      ++return_value : 0); // checks if just run without console arguments.
  std::vector<token> all_tokens = {}; // TODO turn into an array or smthg that's better
  { // Scope operator to clear ram, since else, ifstream etc. will stay till the end of main.
    std::ifstream source_file(argv[1], std::ios::in);
    (void) (!source_file ? std::cerr << "Faled to open file " << argv[1] << "\n",
        ++return_value   : 0);

    uint8_t current_type = 0; // 0 corresponds to token_sep
    std::string current_string = "";

    if (return_value)
      return return_value;

    char character;
    while (!(flags & (1 << 4))) {
      (void) (!source_file.get(character) ? flags |= (1 << 4), flags |= (1 << 3),
          (void) (character = 'F')        : (void) 0);

      is_stringLiteral(character, flags);
      is_charLiteral(character, flags);
      is_escapeSequence(character, flags);

      if (character == '\n' && flags & (1 << 2)) {
        flags |= (1 << 3);
      } else if (character == '\n' || character == '\t' || character == ' ') {
        current_string += ' ';
      } else if (!(flags & 1) && !(flags & (1 << 1))
          && (character == '(' || character == ')' || character == '[' || character == ']'
              || character == '{' || character == '}' || character == ';' || character == '|'
              || character == '&')) {
        if (current_type == token_sep && !(flags & (1 << 4)) && !(flags & (1 << 3))) {
          current_string += character;
        } else {
          /* To be extracted */
          flush_andNew(current_string, current_type, all_tokens, character, token_sep);
          flags &= ~(1 << 2);
        }
      } else {
        (void) (current_type == token_seg ? flags & (1 << 4) // No touchy, you'll fuck this up
            ? (void) (flags = flags & (1 << 3) ? flags & ~(1 << 3) : flags),
            (void) (flush_andNew(current_string, current_type, all_tokens, character, token_seg)),
            (void) (flags = character == '#' ? flags | (1 << 2) : flags & ~(1 << 2))
            : flags & (1 << 3) ? ((void) (flags = flags & (1 << 3) ? flags & ~(1 << 3) : flags),
                  (void) (flush_andNew(
                      current_string, current_type, all_tokens, character, token_seg)),
                  (void) (flags = character == '#' ? flags | (1 << 2) : flags & ~(1 << 2)))
                               : (void) (current_string += character)
                               : (void) (flags = flags & (1 << 3) ? flags & ~(1 << 3) : flags),
            (void) (flush_andNew(current_string, current_type, all_tokens, character, token_seg)),
            (void) (flags = character == '#' ? flags | (1 << 2) : flags & ~(1 << 2)));
      }
    }

    source_file.close();
    assert (!source_file.is_open());
  }

  for (size_t i = 0; i < all_tokens.size(); i++) {
    all_tokens[i].contents = trim_spaces(all_tokens[i].contents, all_tokens[i].type);
  }

  uint32_t longest_separatorChain = 0;
  uint32_t longest_segment = 0;
  for (size_t i = 0; i < all_tokens.size(); i++)
    (void) (all_tokens[i].type == token_seg
            ? longest_segment = all_tokens[i].contents.length() > longest_segment
                ? all_tokens[i].contents.length()
                : longest_segment
            : longest_separatorChain = all_tokens[i].contents.length() > longest_separatorChain
                ? all_tokens[i].contents.length()
                : longest_separatorChain);

  std::string output_fileName = std::string(argv[1]) + ".out";
  std::ofstream target_file(output_fileName.c_str(), std::ios::out);
  (void) (!target_file ? std::cerr << "Failed to create file " << argv[1] << ".out"
                                   << "\n",
      ++return_value : 0);

  if (return_value)
    return return_value;

  {
    uint32_t white_space = 0;
    uint8_t last_type = token_seg;
    file_creator(
        all_tokens, last_type, longest_separatorChain, target_file, white_space, longest_segment);
  }
  target_file.close();

  assert (!target_file.is_open());

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

void file_creator(std::vector<token>& all_tokens, uint8_t& last_type,
    const uint32_t& longest_separatorChain, std::ofstream& target_file, uint32_t& white_space,
    const uint32_t& longest_segment) {
  for (size_t i = 0; i < all_tokens.size(); i++) {
    if (all_tokens[i].type) {
      if (last_type == token_seg) {
        for (size_t j = 0; j < longest_separatorChain; j++) target_file << " ";
        target_file << " ";
      }

      white_space = longest_segment - all_tokens[i].contents.length();

      for (size_t j = 0; j < white_space / 2; j++) target_file << " ";

      target_file << all_tokens[i].contents << "\n";
      last_type = token_seg;
      goto here;
    }
    (void) (last_type == token_sep ? (void) (target_file << "\n") : (void) 0);

    white_space = longest_separatorChain - all_tokens[i].contents.length();
    target_file << all_tokens[i].contents;

    for (size_t j = 0; j < white_space; j++) target_file << " ";
    target_file << " ";

    last_type = token_sep;
  here:
    (void) 0; // noop to silence warning. Will be optimised out if >O1
  }
}

void flush_andNew(std::string& current_string, uint8_t& current_type,
    std::vector<token>& all_tokens, char& character, const uint8_t val) {
  token new_token;
  new_token.contents = current_string;
  new_token.type = current_type;
  all_tokens.push_back(new_token);

  current_string = "";
  current_type = val;

  current_string += character;
}
