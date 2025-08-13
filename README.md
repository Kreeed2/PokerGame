# .NET Poker Game

This project is a client-server multiplayer poker game (Texas Hold'em), migrated from an original Java application to .NET 9.

## Project Structure

The solution is organized into three projects:
-   `PokerProject.Shared`: A class library containing the shared code between the client and server, such as data structures for cards, stacks, and network messages.
-   `PokerProject.Server`: A .NET console application that runs the poker game server. It handles game logic, player connections, and betting.
-   `PokerProject.Client`: A .NET console application that serves as the client for the game. Players can connect to the server and play poker through a text-based interface.

## How to Build

To build the project, you need the .NET 9 SDK installed. Run the following command from the root directory:

```bash
dotnet build
```

## How to Run

### Server

To run the server, execute the following command:

```bash
dotnet run --project Server.Dotnet/PokerProject.Server.csproj
```

The server will start and listen for connections on port 25565.

### Client

To run the client, open a new terminal and execute the following command:

```bash
dotnet run --project Client.Dotnet/PokerProject.Client.csproj
```

You will be prompted to enter the server's IP address.

## Current Status

This project is a complete migration of a Java poker game. The entire codebase has been translated to C# and re-architected to use modern .NET features.

**IMPORTANT:** Due to limitations in the development environment, the migrated code has **not been compiled or tested**. It is likely to contain bugs and require further debugging. This README provides instructions on how the application *should* be run, but it has not been verified.
