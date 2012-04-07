require 'Date'
require 'mullet/view'

module Views

  class Now < Mullet::View

    def full_name()
      return "#{first_name} #{last_name}"
    end

    def now()
      return Date.today.iso8601
    end
  end

end
