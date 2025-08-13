using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace PokerProject.Server
{
    public static class UserDataManager
    {
        private static readonly string UserDataFile = "userdata.txt";

        public static async Task<bool> IsPassAcceptedAsync(string name, string pass)
        {
            if (string.IsNullOrEmpty(pass)) return false;

            if (!File.Exists(UserDataFile)) return true; // No users yet

            var lines = await File.ReadAllLinesAsync(UserDataFile);
            foreach (var line in lines)
            {
                var parts = line.Split(':');
                if (parts.Length == 2 && parts[0] == name)
                {
                    return parts[1] == pass;
                }
            }
            return true; // User doesn't exist, so any pass is fine
        }

        public static async Task<bool> IsNameAcceptedAsync(string name, IEnumerable<Player> players)
        {
            if (string.IsNullOrEmpty(name)) return false;
            if (players.Any(p => p.Name == name && p.Name != "ERROR")) return false;

            if (!File.Exists(UserDataFile)) return true;

            var lines = await File.ReadAllLinesAsync(UserDataFile);
            return !lines.Any(line => line.StartsWith($"{name}:"));
        }

        public static async Task AddUserAsync(string name, string pass)
        {
            if (string.IsNullOrEmpty(name) || string.IsNullOrEmpty(pass) || name == "ERROR") return;

            var userLine = $"{name}:{pass}";

            var lines = new List<string>();
            if (File.Exists(UserDataFile))
            {
                lines.AddRange(await File.ReadAllLinesAsync(UserDataFile));
            }

            if (!lines.Contains(userLine))
            {
                lines.Add(userLine);
                await File.WriteAllLinesAsync(UserDataFile, lines);
            }
        }
    }
}
