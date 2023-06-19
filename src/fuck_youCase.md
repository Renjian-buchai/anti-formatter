# fuck_youCase

The fuck_youCase is a casing format that is unique to this repository. The fuck_youCase follows the following rules:

- The words are split into the following groups:
  - First word
  - Next 3 words, recursively until a word ends with k, after which the group is immediately broken, and the following groups consist of 2 words each
    - For instance, the text `parsing thing that breaks things into tokens because fuck you thats why` broken into groups will be: `parsing`, `thing that breaks`, `things into tokens`, `because fuck`, `you thats`, `why`
- Each group is in snake case. That is, they are separated by underscores(_)
  - For instance, the text `parsing thing that breaks things into tokens because fuck you thats why` with underscores would be: `(parsing)_(thing that breaks)_(things into tokens)_(because fuck)_(you thats)_(why)`, where groups are represented by brackets
- The single-word group at the front is not capitalised
- Three-word groups have their **second** word capitalised. No spaces should exist in the groups. This rule applies to groups terminated early by the existence of words ending in k
- Two-word groups have their **first** word capitalised
- Following all the rules above, the text `parsing_thingThatbreaks_thingsIntotokens_becauseFuck_Youthats_Why`
