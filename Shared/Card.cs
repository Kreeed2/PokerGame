namespace PokerProject.Shared;

public class Card
{
    public CardValue Value { get; }
    public CardColor Color { get; }

    public Card(CardValue value, CardColor color)
    {
        Value = value;
        Color = color;
    }

    public override string ToString()
    {
        return $"{Value.ToString().ToLower()} of {Color.ToString().ToLower()}";
    }
}
