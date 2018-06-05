defmodule Parser do
  use GenServer

  def start_link(filename) do
    :gen_server.start_link({:global, :wc_parser}, __MODULE__, filename, [])
  end

  def request_page(pid) do
    :gen_server.cast({:global, :wc_parser}, {:request_page, pid})
  end

  def processed(ref) do
    :gen_server.cast({:global, :wc_parser}, {:processed, ref})
  end

  def init(filename) do
    xml_parser = Pages.start_link(filename)
    {:ok, {HashDict.new, xml_parser}}
  end

  def handle_cast({:request_page, pid}, {pending, xml_parser}) do
    new_pending = deliver_page(pid, pending, Pages.next(xml_parser))
    {:noreply, {new_pending, xml_parser}}
  end

  def handle_cast({:processed, ref}, {pending, xml_parser}) do
    new_pending = Dict.delete(pending, ref)
    {:noreply, {new_pending, xml_parser}}
  end

  # The guard clause means the xml parser has finished parsing the pages
  defp deliver_page(pid, pending, nil) do
    if Enum.empty?(pending) do
      pending # Nothing to do
    else
      # Why do we need to send an already processing page to the counter
      # if there still pages being processed.
      # Fault tolerance. If a Counter exits or the network goes down or
      # the machine it’s running on dies, we’ll just end up sending the
      # page it was processing to another Counter. Because each page has
      # a reference associated with it, we know which pages have been
      # processed and won’t double-count.
      # To convince yourself, try starting a cluster. On one machine,
      # start a Parser AND an Accumulator. On two other machines, start
      # a number of Counters. If you pull the network cable out the
      # back of one of the machines running counters, or kill the virtual
      # machine they’re running in, the remaining counters will continue
      # to process pages, including those that were in progress on that
      # machine.
      {ref, prev_page} = List.last(pending)
      Counter.deliver_page(pid, ref, prev_page)

      #remove and re-add to pending so it becomes the youngest entry
      Dict.put(Dict.delete(pending, ref), ref, prev_page)
    end
  end

  defp deliver_page(pid, pending, page) do
    ref = make_ref()
    Counter.deliver_page(pid, ref, page)
    Dict.put(pending, ref, page)
  end
end
