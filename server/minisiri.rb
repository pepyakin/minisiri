require 'rubygems'
require 'bundler/setup'
require 'em-websocket'
require 'yajl'
require 'yajl/json_gem'

require './weighted_directed_graph'
require './markov_chain'
require './text_generator'

seed_text = File.open('frankenstein.txt') { |f| f.read }

words = seed_text.split(' ')
seed = words.at(Random.rand(words.length)).gsub(/[^A-Za-z]/, '')

#puts text

EM.run {
  EM::WebSocket.run(:host => "0.0.0.0", :port => 8080) do |ws|
    ws.onmessage { |msg|
      begin
        message = Yajl::Parser.parse(msg)
      rescue
        puts "Invalid JSON #{msg}"
        next
      end
      
      event_name = message[0]
      if event_name == "siri.ask"
        event_id, event_params = message[1],message[2]
        text = RandomText::TextGenerator.new.seed(seed_text).generate(seed)
        ws.send(["result",event_id,{"answer" => text}].to_json)
      end
    }
  end
}