namespace PokerProject.Client
{
    public interface IGameClient
    {
        void DisplayMessage(string message);
        void SetBetRequest(string message);
        void UpdatePlayerChips(string playerName, int chips);
        void UpdatePot(int pot);
        void UpdatePlayerRole(string playerName, string role);
        void AddHandCard(string card);
        void AddOpenCard(string card);
        void UpdatePlayerBet(string playerName, int bet);
        void UpdatePlayerInRound(string playerName, bool inRound);
        void StartNewRound();
        void RequestNameAndPassword();
        void ShowNameTakenError();
        void ShowWrongPasswordError();
        void Exit();
    }
}
