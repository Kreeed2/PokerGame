namespace PokerProject.Server
{
    public class DatabaseObject
    {
        public string Name { get; }
        public string Pass { get; }

        public DatabaseObject(string input)
        {
            var parts = input.Split(':');
            if (parts.Length == 2)
            {
                Name = parts[0];
                Pass = parts[1];
            }
        }

        public override string ToString()
        {
            return $"{Name}:{Pass}\n";
        }
    }
}
