library ieee;
  use ieee.std_logic_1164.all;
  use ieee.numeric_std.all;

entity stopwatch is
  port (clk            : in  std_logic;                    -- clk is "fast enough".
        reset          : in  std_logic;                    -- reset is active high.
        hundradelspuls : in  std_logic;
        muxfrekvens    : in  std_logic;                    -- tic for multiplexing the display.
        start_stopp    : in  std_logic;
        nollstallning  : in  std_logic;                    -- restart from 00:00:00
        visningslage   : in  std_logic;                    -- 1:show min/sec. 0: show sec/centisec
        seg            : out std_logic_vector(6 downto 0); -- Segments
        dp             : out std_logic;                    -- Decimal point
        an             : out std_logic_vector(3 downto 0); -- Digit to display
        raknar         : out std_logic                     -- Connected to an LED
       );
end entity;

architecture rtl of stopwatch is
   
 signal sync_hundra1  : std_logic;
 signal sync_hundra2  : std_logic;
 signal enpuls_hundra : std_logic;
 signal sync_mux1  : std_logic;
 signal sync_mux2  : std_logic;
 signal enpuls_mux : std_logic;
 
 signal hundredths : unsigned(3 downto 0);  -- 4 bits for 0-9
 signal carry_hundredths : std_logic;
 signal tenths : unsigned(3 downto 0);
signal carry_tenths : std_logic;
signal seconds : unsigned(3 downto 0);
signal carry_seconds : std_logic;
signal tens_seconds : unsigned(2 downto 0);  -- 3 bits for 0-5
signal carry_tens_seconds : std_logic;
signal minutes : unsigned(3 downto 0);
signal carry_minutes : std_logic;
signal tens_minutes : unsigned(2 downto 0);  -- 3 bits for 0-5

type rom is array (0 to 9) of std_logic_vector(6 downto 0);
  constant mem : rom := (
    "1000000", -- 0
    "1111001", -- 1
    "0100100", -- 2
    "0110000", -- 3
    "0011001", -- 4
    "0010010", -- 5
    "0000010", -- 6
    "1111000", -- 7
    "0000000", -- 8
    "0010000" -- 9
  );
  
begin

process(clk)
begin
    if rising_edge(clk) then
        sync_hundra1 <= hundradelspuls;
        sync_hundra2 <= sync_hundra1;
        sync_mux1 <= muxfrekvens;
        sync_mux2 <= sync_mux1;
    end if;
    end process;
    
-- enpulsad hundradelspuls och muxfrekvens
enpuls_hundra <= sync_hundra1 and not sync_hundra2;
enpuls_mux <= sync_mux1 and not sync_mux2;

-- Kontroll av LED status (klockan igång eller stoppad)
process(clk)
 begin
     if rising_edge(clk) then
         if start_stopp = '1' then
             raknar <= '1';  -- Tänd LED när klockan är igång
         else
             raknar <= '0';  -- Släck LED när klockan är stoppad
         end if;
     end if;
 end process;
    
-- Hundredths counter (0-9)
process(clk, reset)
begin
    if reset = '1' then
        hundredths <= (others => '0');
    elsif rising_edge(clk) then
        if enpuls_hundra = '1' and start_stopp = '1' then
            if hundredths = 9 then
                hundredths <= (others => '0');
            else
                hundredths <= hundredths + 1;
            end if;
        end if;
    end if;
end process;

carry_hundredths <= '1' when (enpuls_hundra = '1' and hundredths = 9) else '0';

-- Tenths counter (0-9)
process(clk, reset)
begin
    if reset = '1' then
        tenths <= (others => '0');
    elsif rising_edge(clk) then
        if carry_hundredths = '1' and start_stopp = '1' then
            if tenths = 9 then
                tenths <= (others => '0');
            else
                tenths <= tenths + 1;
            end if;
        end if;
    end if;
end process;

carry_tenths <= '1' when (carry_hundredths = '1' and tenths = 9) else '0';

-- Seconds counter (0-9)
process(clk, reset)
begin
    if reset = '1' then
        seconds <= (others => '0');
    elsif rising_edge(clk) then
        if carry_tenths = '1' and start_stopp = '1' then
            if seconds = 9 then
                seconds <= (others => '0');
            else
                seconds <= seconds + 1;
            end if;
        end if;
    end if;
end process;

carry_seconds <= '1' when (carry_tenths = '1' and seconds = 9) else '0';

-- Tens of seconds counter (0-5)
process(clk, reset)
begin
    if reset = '1' then
        tens_seconds <= (others => '0');
    elsif rising_edge(clk) then
        if carry_seconds = '1' and start_stopp = '1' then
            if tens_seconds = 5 then
                tens_seconds <= (others => '0');
            else
                tens_seconds <= tens_seconds + 1;
            end if;
        end if;
    end if;
end process;

carry_tenths <= '1' when (carry_seconds = '1' and tens_seconds = 5) else '0';

-- Minutes counter (0-9)
process(clk, reset)
begin
    if reset = '1' then
        minutes <= (others => '0');
    elsif rising_edge(clk) then
        if carry_tens_seconds = '1' and start_stopp = '1' then
            if minutes = 9 then
                minutes <= (others => '0');
            else
                minutes <= minutes + 1;
            end if;
        end if;
    end if;
end process;

carry_minutes <= '1' when (carry_tens_seconds = '1' and minutes = 9) else '0';

-- Tens of minutes counter (0-5)
process(clk, reset)
begin
    if reset = '1' then
        tens_minutes <= (others => '0');
    elsif rising_edge(clk) then
        if carry_minutes = '1' and start_stopp = '1' then
            if tens_minutes = 5 then
                tens_minutes <= (others => '0');
            else
                tens_minutes <= tens_minutes + 1;
            end if;
        end if;
    end if;
end process;

end architecture;
