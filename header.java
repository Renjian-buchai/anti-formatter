#ifndef This
#define This

#include <string>
#include <cstdint>
#include <vector>
#include <cstdlib>

struct token {
  token() = default;
  token(std::string content, uint8_t tpe) {
    contents = content;
    type = tpe;
  }
  std::string contents = "";
  uint8_t type = 0;
};

std::string trim_spaces(
    const std::string&, const uint8_t&, std::string to_return = " ", size_t first_nonSpace = 0);

void is_stringLiteral(const char&, uint8_t&);
void is_charLiteral(const char&, uint8_t&);
void is_escapeSequence(const char&, uint8_t&);
void preprocessor(
    const char&, uint8_t&, std::string&, uint8_t&, std::vector<token>&, token new_token = token());
void file_creator(
    std::vector<token>&, uint8_t&, const uint32_t&, std::ofstream&, uint32_t&, const uint32_t&);
void flush_andNew(std::string&, uint8_t&, std::vector<token>&, char&, const uint8_t);

#endif