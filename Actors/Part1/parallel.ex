defmodule Parallel do
  def map(collection, fun) do
    parent = self()

    processes = Enum.map(collection, fn(e) ->
      spawn_link(fn() ->
        send(parent, { self(), fun.(e) })
      end)
    end)

    Enum.map(processes, fn(pid) ->
      # Receive pauses the program and waits for a message.
      # It continues executing when ONE message is received.
      # Hence, to receive all the messages, we need to iterate
      # over each process and call receive that many times.
      receive do
        {^pid, result} -> result
      end
    end)
  end
end


# Test via IEX

# # from dir where paralle.ex is located
# import_file("parallel.ex")
# slow_double = fn(x) -> :timer.sleep(1000); x * 2 end
# :timer.tc(fn() -> Enum.map([1, 2, 3, 4], slow_double) end)
# :timer.tc(fn() -> Parallel.map([1, 2, 3, 4], slow_double) end)
