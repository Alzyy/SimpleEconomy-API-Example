package it.alzy.examplePlugin;

import it.alzy.simpleeconomy.api.EconomyProvider;
import it.alzy.simpleeconomy.api.SimpleEconomyAPI;
import it.alzy.simpleeconomy.api.TransactionResult;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example plugin showcasing the use of SimpleEconomy API.
 * Implements a basic command to demonstrate economy features like:
 * - Depositing money
 * - Checking balance
 * - Checking if player has enough money
 * - Transferring money
 */
public final class Example extends JavaPlugin implements CommandExecutor {

    // Holds the economy provider from SimpleEconomy plugin
    private static EconomyProvider economyProvider;

    /**
     * Called when the plugin is enabled.
     * Sets up the command executor and checks for SimpleEconomy plugin.
     */
    @Override
    public void onEnable() {
        // Register the "example" command and set this class as its executor
        getCommand("example").setExecutor(this);

        // Check if SimpleEconomy plugin is installed on the server
        if(Bukkit.getPluginManager().getPlugin("SimpleEconomy") != null) {
            getLogger().info("SimpleEconomy found, enabling economy features.");
            // Get the economy provider to interact with player balances
            economyProvider = SimpleEconomyAPI.getProvider();
        } else {
            getLogger().info("SimpleEconomy not found!");
            economyProvider = null;
        }
    }

    /**
     * Called when the plugin is disabled.
     * Here you can perform cleanup tasks if needed.
     */
    @Override
    public void onDisable() {
        if(economyProvider != null) {
            economyProvider = null; // Clear the reference to the economy provider
        }
    }

    /**
     * Handles the /example command.
     * Supports subcommands to demonstrate different economy operations.
     *
     * @param sender The sender of the command
     * @param command The command executed
     * @param label The alias used
     * @param args Command arguments
     * @return true if command executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the command is executed by a player, not the console
        if(!(sender instanceof Player)) return false;

        // If economy provider is not available, exit gracefully
        if(economyProvider == null) return true;

        Player player = (Player) sender;

        if(args.length == 0) { // No subcommand provided
            player.sendMessage("Please provide a subcommand: testgive, testbalance, testenough, testtransfer");
            return true;
        }

        // Check the first argument to determine which subcommand was used
        switch(args[0].toLowerCase()) {
            case "testgive":
                // Give 10 currency units to the player
                economyProvider.deposit(player.getUniqueId(), 10);
                player.sendMessage("You have been given 10 currency units!");
                break;

            case "testbalance":
                // Get player's balance asynchronously and send it as a message
                economyProvider.getBalance(player.getUniqueId()).thenAccept(bal -> {
                    player.sendMessage("Your balance is: " + bal);
                });
                break;

            case "testenough":
                // Check if player has at least 10000 currency units
                economyProvider.hasEnough(player.getUniqueId(), 10000).thenAccept(hasEnough -> {
                    if(hasEnough) {
                        player.sendMessage("You have enough money!");
                    } else {
                        player.sendMessage("You don't have enough money!");
                    }
                });
                break;

            case "testtransfer":
                // Example of transferring money (here it's just to themselves for demonstration)
                double amount = 10;
                economyProvider.transfer(player.getUniqueId(), player.getUniqueId(), amount)
                        .thenAccept(success -> {
                            if(success.equals(TransactionResult.SUCCESS)) {
                                player.sendMessage("You have paid " + amount + " to " + player.getName());
                                player.sendMessage("You have received " + amount + " from " + player.getName());
                            } else if(success.equals(TransactionResult.INSUFFICIENT_FUNDS)){
                                player.sendMessage("Transaction failed! Do you have enough money?");
                            } else {
                                player.sendMessage("Transaction failed!");
                            }
                        });
                break;

            default:
                // Inform player if the subcommand is invalid
                player.sendMessage("Unknown subcommand. Available: testgive, testbalance, testenough, testtransfer");
                break;
        }
        return true;
    }
}
