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
