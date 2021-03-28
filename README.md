# Command Attack

Run commands when hitting / getting hit by players.

https://bukkit.org/threads/commandattack.490563/

## Placeholders

Placeholder | Description
--- | ---
`attacker` | The attacker/person doing damage
`target` | The target/person taking damage

## Commands

Command | Permission | Description | Parameters
--- | --- | --- | ---
`ca <command> [arguments] <sudo_required>` | `ca.create` | Create a command bound to an item |  `command`: The command to execute. <br /> `arguments`: Optional arguments to pass to the command. <br /> `sudo_required` Whether the sudo permission is required to use this command attack. Accepted values: `yes/y/true/t/no/n/false/f`.
`calist` | `ca.list` | Lists the id and command bound to the item in the players hand. | 
`caremove <id>` | `ca.remove` | Remove the command that has id `<id>` | `id`: The id of the command to be removed.
`careload` | `ca.reload` | Not yet implemented! | 

## Permissions

Permission | Description
--- | ---
`ca.create` | Allows you to bind commands to items
`ca.list` | Allows you to list the command bound to an item
`ca.remove` | Allows you to remove commands from items
`ca.reload` | Allows you to reload the config
`ca.sudo` | Allows you to use sudo/restricted command attacks
`ca.*` | Gives access to all CommandAttack permissions

## Config

Option | Description
--- | ---
version | 
message_prefix | Text to prefix all messages
messages | Configuration for message text, broken down into categories <br /> Messages use [MessageFormat](https://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html) and [ColorCodes](https://minecraft.fandom.com/wiki/Formatting_codes#Color_codes). The `&` character can be used in place of `§` for color codes.

### Example
```yaml
version: 0.2.0
message_prefix: "&6[CA]"
messages:
  common:
    noPermission: "&cYou do not have permission to use this command."
    notPlayer: "&cYou need to be a player to use this command."
    notImplemented: "&cNot yet implemented!."
    argLengthMismatch_Ignoring: "&eFound &6{0}&e arguments, but was expecting &6{1}&e, ignoring arguments."
    argLengthMismatch: "&cFound &4{0}&c arguments, but was expecting &4{1}&c."
    argLengthMin: "&cFound &4{0}&c arguments, but was expecting at least &4{1}&c."
  calist:
    noCommandsForItem: "&cNo commands for item &4{0}&c."
    foundCommandForItem: "&aFound command for item &2{0}&a:\nID: &2{1}&a\nCommand: &2{2}"
  caremove:
    invalidID: "&cInvalid id &4{0}&c."
    couldntFindCommand: "&cCould not find command with id &4{0}&c."
    success: "&aRemoved successfully:\nMaterial: &2{0}&a\nID: &2{1}&a\nCommand: &2{2}"
  careload:
    success: "&aSuccessfully reloaded config."
    invalidConfigOption: "&cInvalid configuration value at &4{0}&c."
  ca:
    invalidBoolean: "&cInvalid argument &4{0}&c.\nWas expecting one of [yes,y,true,t,no,n,false,f]."
    success: "&aSuccessfully bound command."
    templateMismatchedBraces: "&cMismatched braces."
    templateInvalidPlaceholder: "&cInvalid placeholder &4{0}&c."
```

