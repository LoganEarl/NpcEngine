# Social Exchanges

AI controllers care about social meaning behind interactions. They care if you are building or destroying rapport, threatening or bribing, etc. To make this possible there is a generic "SocialEffect" class. This represents the social categories that a given action might fit into, as well as the severity of each of those categories. 

In general, every mapping of enum -> number will follow some conventions
- Each enum will represent a spectrum of feeling. E.g. ANGRY_CALM
- Each number will be bounded from -1 to +1
- Negative numbers will correspond to negative emotions. Positive to positive
- Each enum will be named starting with the negative side of the axis to the positive side of the axis
- In general, each axis will follow a rule of diminishing returns, where maxing out or mining out a value is very difficult