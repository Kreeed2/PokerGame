using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PokerProject.Shared
{
    public class Stack
    {
        private List<Card> cards = new List<Card>();

        public void FillStack()
        {
            cards.Clear();

            foreach (CardColor color in Enum.GetValues(typeof(CardColor)))
            {
                foreach (CardValue value in Enum.GetValues(typeof(CardValue)))
                {
                    cards.Add(new Card(value, color));
                }
            }

            Shuffle();
        }

        public void Shuffle()
        {
            Random rng = new Random();
            int n = cards.Count;
            while (n > 1)
            {
                n--;
                int k = rng.Next(n + 1);
                Card value = cards[k];
                cards[k] = cards[n];
                cards[n] = value;
            }
        }

        public void AddStack(Stack stack)
        {
            cards.AddRange(stack.cards);
        }

        public Card? Remove()
        {
            if (cards.Count == 0)
            {
                return null;
            }

            Card card = cards[0];
            cards.RemoveAt(0);
            return card;
        }

        public void Add(Card c)
        {
            cards.Add(c);
        }

        public List<Card> Cards => cards;

        public void Clear()
        {
            cards.Clear();
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            foreach (Card c in cards)
            {
                sb.AppendLine($"\t{c.ToString()}");
            }
            return sb.ToString();
        }
    }
}
